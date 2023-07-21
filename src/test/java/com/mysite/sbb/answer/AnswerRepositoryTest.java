package com.mysite.sbb.answer;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnswerRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void testSave() {
        //given
        //질문이 있고나서 답변이 있을 수 있으니까
        Optional<Question> questionOp = questionRepository.findById(2);
        assertTrue(questionOp.isPresent()); //만약 여기서 false라면 테스트 중단됨 -> get에서 NPE발생할 일이 없겟지
        Question question = questionOp.get();

        Answer answer = Answer.builder().content("내일 날씨는 덥다고 하네요 ㅎㅎㅎ").createDate(LocalDateTime.now())
                .question(question).build();

        //when
        answerRepository.save(answer);

        //then
        assertThat(answerRepository.count()).isEqualTo(1);

    }

    @Test
    void testfindAll() { //select는 쫌...
        List<Answer> all = answerRepository.findAll();

        assertThat(all).size().isEqualTo(1);
    }

    @Test
    @DisplayName("답변에 연결된 질문 찾기") //단방향이라 이 경우는 easy
    void testFindQuestionByAnswer() {
        //given
        Optional<Answer> answerOp = answerRepository.findById(1);
        assertTrue(answerOp.isPresent());
        Answer answer = answerOp.get();

        //when
        Question question = answer.getQuestion();

        //then
        assertThat(question.getId()).isEqualTo(2);
    }


    /**
     * ailed to lazily initialize a collection of role:
     * com.mysite.sbb.question.Question.answerList, could not initialize proxy - no Session
     *
     * Q -> A  (OneToMany)는 기본값이 lazy전략
     */
    @Test
    @DisplayName("질문에 연결된 답변 찾기") //양방향~~~ difficult
    @Transactional
    void testFindAnswerByQuestion() {
        //given
        Optional<Question> questionOp = questionRepository.findById(2); //<---lazy전략이라 DB세션 종료
        assertTrue(questionOp.isPresent());
        Question question = questionOp.get();

        //lazy전략은 사용안하는 answer조회 쿼리를 미리 보내지않음! 사용할 때 보냄
        //근데, db세션은 question조회하고 끝나버렸기 때문에 문제가 발생!
        //그래서 OneToMany는 lazy 전략이 기본값이라 eager전략으로 옵션을 줘야되 --- 방법1 (엔티티에 설정) <-- 해봤는데 통과!
        //근데, 이게 많은 문제가 발생하기때문에 실제서버들은 DB세션이 종료가 안되도록 되있음
        //테스트코드에서는 그게 안되서 이게 한 단위의 작업이라는 @Transitional 옵션 걸어주기  ---- 방법2

        //when
        List<Answer> answerList = question.getAnswerList(); //<-- 이때 answer조회 쿼리 날림

        //then
        assertThat(answerList).size().isEqualTo(1);
    }
}