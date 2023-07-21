package com.mysite.sbb.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Integer> {

    List<Question> findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content); //여러 칼럼이 일치하는 경우
    List<Question> findBySubjectLike(String subject);
}
