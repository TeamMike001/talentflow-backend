package LearnX.com.example.LearnX.dtos;

import LearnX.com.example.LearnX.Enum.Role;
import lombok.Data;



public record UserRegistrationDto (String name, String email,String password,Role role){
}
