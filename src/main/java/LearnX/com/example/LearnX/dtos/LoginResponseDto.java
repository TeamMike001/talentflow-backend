package LearnX.com.example.LearnX.dtos;


public record LoginResponseDto(
    String token,
    UserResponseDto user){
}