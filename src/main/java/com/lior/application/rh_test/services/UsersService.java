package com.lior.application.rh_test.services;

import com.lior.application.rh_test.aspect.util.DataTypes;
import com.lior.application.rh_test.aspect.util.OwnershipFilter;
import com.lior.application.rh_test.dto.UserCRUDDTO;
import com.lior.application.rh_test.dto.UserDTO;
import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.repos.UsersRepository;
import com.lior.application.rh_test.util.CurrentUser;
import com.lior.application.rh_test.util.PassGenerator;
import com.lior.application.rh_test.util.Roles;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import com.lior.application.rh_test.util.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final PassGenerator passGenerator;
    private final PasswordEncoder passwordEncoder;


    /**
     * Gets all users
     * @param page page number (starting with 1
     * @param usersPerPage How many users displayed per page
     * @return Page of users
     */
    public Page<UserDTO> getAllUsers(int page, int usersPerPage){
        return usersRepository.findAll(PageRequest.of(page-1, usersPerPage))
                .map((user) -> modelMapper.map(user, UserDTO.class));
    }

    /**
     * Gets user by username
     * @param username username
     * @return Optional of User
     */
    @Cacheable(value = "users", key = "#username")
    public UserDTO findUserByUsername(String username){
        return toDTO(username == null || username.isBlank() ? CurrentUser.get():
                usersRepository.findByUsername(username).orElseThrow(NotFoundException::new));
    }

    public UserDTO findOne(int id) {
        return toDTO(usersRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    /**
     * Saving new user in database
     * @param user User to be saved
     */
    public String save(UserCRUDDTO user) {
        String password = "";
        if (user.getPassword() == null) {
            password = passGenerator.generatePassayPassword();
            log.info("Password for user \"" +user.getUsername()+"\" wasn't specified. Automatically generated password \"" +
                    password + "\" was set.");
            user.setPassword(password);
            File credentials = new File("credentials/"+ user.getUsername()+".txt");
            try (FileWriter writer = new FileWriter(credentials)){
                credentials.createNewFile();
                writer.write("username: " + user.getUsername() + "\n");
                writer.write("password: " + user.getPassword());
                writer.flush();
            }catch (IOException e){
                log.error("File creation error",e);
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(toUser(user));
        return password;
    }

    /**
     * Updates user with given ID
     * @param id id of user to be updated
     * @param updUser New user information
     */
    @OwnershipFilter(datatype = DataTypes.User)
    public void updateById(int id, UserCRUDDTO updUser)  {
        User user = toUser(updUser);
        user.setId(id);
        if (!Objects.equals(CurrentUser.get().getRole(), Roles.ROLE_ADMIN)) {
            user.setRole(usersRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User whith id " + id + "not found"))
                    .getRole());
        }
        usersRepository.save(user);
    }
    /**
     *
     * Updates user with given username
     * @param username Username of user to be updated
     * @param updUser New user information
     */

    @OwnershipFilter(datatype = DataTypes.User)
    public void updateByUsername(UserCRUDDTO updUser, String username)  {
        User user = toUser(updUser);
        User old = usersRepository.findByUsername(username).orElseThrow(NotFoundException::new);
        user.setId(old.getId());
        if (!Objects.equals(CurrentUser.get().getRole(), Roles.ROLE_ADMIN)) {
            user.setRole(old.getRole());
        }
        usersRepository.save(user);
    }

    /**
     * Deletes user by ID
     * @param id ID of user to be deleted
     */
    @OwnershipFilter(datatype = DataTypes.User)
    public void delete(int id) {
        usersRepository.deleteById(id);
    }

    /**
     * Deletes user by username
     * @param username Username of user to be deleted
     */
    @OwnershipFilter(datatype = DataTypes.User)
    public void delete(String username) {
        usersRepository.deleteUserByUsername(username);
    }

    private User toUser(UserCRUDDTO userCRUDDTO) {
        return modelMapper.map(userCRUDDTO, User.class);
    }

    private UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
