package LearnX.com.example.LearnX.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record AuthResponseDto (String token, UserResponseDto user) {

}