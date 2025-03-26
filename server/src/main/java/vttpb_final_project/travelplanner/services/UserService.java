package vttpb_final_project.travelplanner.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttpb_final_project.travelplanner.models.UserEntity;
import vttpb_final_project.travelplanner.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    public UserEntity registerUser(UserEntity user){
        return userRepo.save(user);
    }

    public boolean loginUser(String username, String password){
        Optional<UserEntity> opt = userRepo.findByUsername(username);
        if (!opt.isEmpty()){
            return opt.get().getPassword().equals(password);
        }
        return false;
    }
}
