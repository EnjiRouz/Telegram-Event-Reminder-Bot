package main.entity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "events")
public class Event {

    public Event() {
        isOpen = true;
    }

    public Event(Form form, String name, String description, LocalDateTime dateTime) {
        this.form = form;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        isOpen = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "form_id")
    private Form form;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private LocalDateTime dateTime;

    @Column
    private String venue;

    @Column
    private Boolean isOpen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }
}
