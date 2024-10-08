package com.securenotes.repository;

import com.securenotes.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Integer> {
    Notes findByNotesId(int notesId);

    @Query("SELECT n FROM Notes n WHERE n.userId = :loggedInUserId and (n.password IS NULL or n.password = '')" )
    List<Notes> findAllNotesWithoutPasswordByUserId(int loggedInUserId);

    Notes findByNotesIdAndPassword(int notesId, String password);

    @Query("select n from Notes n where n.userId = :loggedInUserId and (n.password is not null  and n.password != '')")
    List<Notes> findAllSecuredNotes(int loggedInUserId);

    //The % appended to the :title acts as a wildcard,
    //allowing matches where the search term appears anywhere in the title.

    //this approach gives only where searchKey starts with - not implementing full text search
//    @Query("select n from Notes n where (n.title like :searchKey or n.description like :searchKey%) and n.userId = :loggedInUserId and n.password is null")
//    List<Notes> searchNotes(String searchKey, int loggedInUserId);
}
