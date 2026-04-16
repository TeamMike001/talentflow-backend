package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Lecture;
import LearnX.com.example.LearnX.Model.Section;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.CourseCreateDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.dtos.LectureDto;
import LearnX.com.example.LearnX.dtos.SectionDto;
import LearnX.com.example.LearnX.dtos.UserSummaryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    private final UserMapper userMapper;

    public CourseMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Course toEntity(CourseCreateDto dto, User primaryInstructor, List<User> additionalInstructors) {
        Course course = new Course();
        course.setTitle(dto.title());
        course.setSubtitle(dto.subtitle());
        course.setCategory(dto.category());
        course.setSubcategory(dto.subcategory());
        course.setTopic(dto.topic());
        course.setLanguage(dto.language());
        course.setLevel(dto.level());
        course.setDuration(dto.duration());
        course.setThumbnailUrl(dto.thumbnailUrl());
        course.setTrailerUrl(dto.trailerUrl());
        course.setDescription(dto.description());
        course.setTeaches(dto.teaches() != null ? new ArrayList<>(dto.teaches()) : new ArrayList<>());
        course.setAudience(dto.audience() != null ? new ArrayList<>(dto.audience()) : new ArrayList<>());
        course.setRequirements(dto.requirements() != null ? new ArrayList<>(dto.requirements()) : new ArrayList<>());
        course.setWelcomeMessage(dto.welcomeMessage());
        course.setCongratsMessage(dto.congratsMessage());
        course.setInstructor(primaryInstructor);
        course.setAdditionalInstructors(additionalInstructors);
        course.setPublished(false);
        course.setCreatedAt(LocalDateTime.now());

        // Map sections
        if (dto.sections() != null) {
            List<Section> sections = dto.sections().stream()
                    .map(sectionDto -> {
                        Section section = new Section();
                        section.setName(sectionDto.name());
                        section.setOrderIndex(sectionDto.orderIndex());
                        section.setCourse(course);

                        // Map lectures
                        if (sectionDto.lectures() != null) {
                            List<Lecture> lectures = sectionDto.lectures().stream()
                                    .map(lectureDto -> {
                                        Lecture lecture = new Lecture();
                                        lecture.setName(lectureDto.name());
                                        lecture.setOrderIndex(lectureDto.orderIndex());
                                        lecture.setVideoUrl(lectureDto.videoUrl());
                                        lecture.setNotes(lectureDto.notes());
                                        lecture.setCaption(lectureDto.caption());
                                        lecture.setDescription(lectureDto.description());
                                        lecture.setAttachmentUrl(lectureDto.attachmentUrl());
                                        lecture.setSection(section);
                                        return lecture;
                                    })
                                    .collect(Collectors.toList());
                            section.setLectures(lectures);
                        }
                        return section;
                    })
                    .collect(Collectors.toList());
            course.setSections(sections);
        }
        return course;
    }

    public CourseResponseDto toResponseDto(Course course) {
        if (course == null) return null;

        // Convert primary instructor
        UserSummaryDto instructorDto = userMapper.toSummaryDto(course.getInstructor());

        List<UserSummaryDto> additionalInstructorsDto = course.getAdditionalInstructors().stream()
                .map(userMapper::toSummaryDto)
                .collect(Collectors.toList());

        List<SectionDto> sectionDtos = course.getSections().stream()
                .map(section -> {
                    List<LectureDto> lectureDtos = section.getLectures().stream()
                            .map(lecture -> new LectureDto(
                                    lecture.getName(),
                                    lecture.getOrderIndex(),
                                    lecture.getVideoUrl(),
                                    lecture.getNotes(),
                                    lecture.getCaption(),
                                    lecture.getDescription(),
                                    lecture.getAttachmentUrl()
                            ))
                            .collect(Collectors.toList());
                    return new SectionDto(section.getName(), section.getOrderIndex(), lectureDtos);
                })
                .collect(Collectors.toList());

        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getSubtitle(),
                course.getCategory(),
                course.getSubcategory(),
                course.getTopic(),
                course.getLanguage(),
                course.getLevel(),
                course.getDuration(),
                course.getThumbnailUrl(),
                course.getTrailerUrl(),
                course.getDescription(),
                course.getTeaches(),
                course.getAudience(),
                course.getRequirements(),
                sectionDtos,
                course.getWelcomeMessage(),
                course.getCongratsMessage(),
                instructorDto,
                additionalInstructorsDto,
                course.isPublished(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}