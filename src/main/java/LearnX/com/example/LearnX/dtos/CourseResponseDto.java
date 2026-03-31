package LearnX.com.example.LearnX.dtos;




public record CourseResponseDto(Long id,String title,String description,String thumbnail,
                                boolean published,UserSummaryDto instructor)  {
}


