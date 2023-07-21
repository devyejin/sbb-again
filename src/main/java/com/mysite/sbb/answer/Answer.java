package com.mysite.sbb.answer;


import com.mysite.sbb.question.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Answer {

    @Id //PK 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //db에서 entity마다 독립적인 시퀀스를 부여하는 정책
    private Integer id;

    @Column(columnDefinition = "TEXT") //글자수 제한 없음
    private String content;
    private LocalDateTime createDate;

    @ManyToOne //fk관리는 answer에서 -> 즉, 답변에서 질문을 조회,  answer.getQuestion().getSubject() 로 접근
    private Question question;
}
