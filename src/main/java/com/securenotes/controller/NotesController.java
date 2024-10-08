package com.securenotes.controller;

import com.securenotes.dto.CreateNoteRequest;
import com.securenotes.dto.NotesResponse;
import com.securenotes.model.Notes;
import com.securenotes.model.User;
import com.securenotes.repository.NotesRepository;
import com.securenotes.service.NotesService;
import com.securenotes.service.OurUserDetailService;
import com.securenotes.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("notes")
public class NotesController {

    @Autowired
    NotesService notesService;

    @Autowired
    OurUserDetailService ourUserDetailService;

    @PostMapping("/add")
    public ResponseEntity<NotesResponse> addNote(@RequestBody CreateNoteRequest createNoteRequest) throws Exception {
        Notes notes = notesService.addNote(createNoteRequest);
        NotesResponse notesResponse = new NotesResponse();
        notesResponse.setTitle(EncryptionUtil.decrypt(notes.getTitle()));
        notesResponse.setDescription(EncryptionUtil.decrypt(notes.getDescription()));
        notesResponse.setCreatedOn(notes.getCreatedOn());
        notesResponse.setUpdatedOn(notes.getUpdatedOn());

        return ResponseEntity.ok(notesResponse );
    }

    @GetMapping("/getAll")
    public List<Notes> getAll(){
        return notesService.getAllNotes();
    }

    @GetMapping("/getAllSecuredNotes")
    public List<NotesResponse>getAllSecured(){
        return notesService.getAllSecuredNotes();
    }

    @GetMapping("/get/{id}")
    public Notes getById(@PathVariable("id") int id) throws Exception {
        return notesService.getNoteById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Notes>deleteNote(@PathVariable("id")int id) throws Exception {
        return ResponseEntity.ok(notesService.delete(id));
    }

    @DeleteMapping("deleteSecureNote/{id}")
    public ResponseEntity<Notes>deleteSecureNote(@PathVariable("id")int id, @RequestParam(required = true) String password) throws Exception {

        return ResponseEntity.ok(notesService.deleteSecuredNote(id, password));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NotesResponse>updateNote(@PathVariable("id")int id,  @RequestBody CreateNoteRequest createNoteRequest) throws Exception {
        return ResponseEntity.ok(notesService.update(id, createNoteRequest));
    }

    @PutMapping("updateSecureNote/{id}")
    public ResponseEntity<NotesResponse>updateSecureNote(@PathVariable("id")int id,
                                                 @RequestParam(required = true) String password,
                                                 @RequestBody CreateNoteRequest createNoteRequest) throws Exception {


        return ResponseEntity.ok(notesService.updateSecuredNote(id, password, createNoteRequest));
    }

    @PutMapping("/setpassword/{id}")
    public ResponseEntity<NotesResponse> setPasswordForNote(@PathVariable int id, @RequestBody CreateNoteRequest createNoteRequest) throws Exception {
        return ResponseEntity.ok(notesService.setPasswordForNote(id,createNoteRequest));
    }

    @GetMapping("/getByIdPassword/{id}")
    public ResponseEntity<NotesResponse> getNoteByIdAndPassword(@PathVariable int id, @RequestBody CreateNoteRequest createNoteRequest) throws Exception {
        return ResponseEntity.ok(notesService.getNoteByIdAndPassword(id, createNoteRequest));
    }

    @GetMapping("/search/{searchKey}")
    public ResponseEntity<List<NotesResponse>>search(@PathVariable("searchKey")String searchKey) throws Exception {
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(notesService.search(searchKey, loggedInUser.getUserId()));
    }


}
