package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public class EventRequestDto {
    private String title;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private int ticketsAvailable = 50;
    private boolean published = true;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    
    public int getTicketsAvailable() { return ticketsAvailable; }
    public void setTicketsAvailable(int ticketsAvailable) { this.ticketsAvailable = ticketsAvailable; }
    
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}

