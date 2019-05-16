package main.entity;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participant")
public class Participant {
    @Id
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

    @Column
    private String name;

    @Column
    private String formData;

    @Column
    private LocalDateTime formCompletionDateTime;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public LocalDateTime getFormCompletionDateTime() {
        return formCompletionDateTime;
    }

    public void setFormCompletionDateTime(LocalDateTime formCompletionDateTime) {
        this.formCompletionDateTime = formCompletionDateTime;
    }
}
