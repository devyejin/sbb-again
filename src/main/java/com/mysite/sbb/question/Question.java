package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@Setter // Entity에는 Setter 사용권장X , Builder 패턴 추천
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY 전략 : DB에서 엔티티마다 개별 시퀀스 객체 부여
    @Column(name="QUESTION_ID")
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT") // 글자수 제한 없음
    private String content;
    private LocalDateTime createDate;

    /**
     * Question -> Answer 조회, question.getAnswerList() (근데 양뱡향은 추천하지 않음)
     * 그리고, 참조무결성원칙때문에 Question을 삭제하려면 Question에 달린 Answer을 다 삭제해야 삭제가 가능;;
     * 번거롭기때문에 Cascade 옵션을 사용해서, 자동으로 동작하도록 하는 기능이 존재
     * CascadeType.REMOVE :  부모를 삭제하면 그에 딸린 자식들을 다 삭제하는 옵션
     *
     *  fetch = FetchType.EAGER
     */
    @OneToMany(mappedBy ="question", cascade = CascadeType.REMOVE) //양방향도 가능 근데 ㅂㅊ
    private List<Answer> answerList = new ArrayList<>();
}
