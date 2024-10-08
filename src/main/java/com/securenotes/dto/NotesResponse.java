package com.securenotes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.securenotes.model.Notes;
import com.securenotes.utils.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NotesResponse {
//    private int notesId;
    private String title;
    private String description;
    private String message;
    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

    public NotesResponse( String title, Date createdOn, Date updatedOn, String description) {
//        notesId = this.notesId;
        title = this.title  ;
        createdOn = this.createdOn;
        updatedOn = this.updatedOn;
        description = this.description;
    }




    public static NotesResponse to (Notes notes) throws Exception {
        return NotesResponse.builder()
                .title(EncryptionUtil.decrypt(notes.getTitle()))
                .description(EncryptionUtil.decrypt(notes.getDescription()))
                .createdOn(notes.getCreatedOn())
                .updatedOn(notes.getUpdatedOn())
                .build();
    }
}
