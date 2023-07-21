package com.mysite.sbb.question;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class QuestionRepositoryTest {

    @Autowired //테스트코드의 경우, 메서드단위로 독립적인 테스트가 이뤄지다보니 생성자를 통한 DI는 어렵다.
    private QuestionRepository questionRepository;

//    @PersistenceContext
//    private EntityManager em;



    @Test
    void testInsert() {

        //given
        Question question = Question.builder().
                            subject("첫번째 질문글").
                            content("내일은 더울까요 추울까요?").
                            createDate(LocalDateTime.now()).
                            build();

        //when
        Question savedQuestion = questionRepository.save(question);

        //then
        assertThat(question.getContent()).isEqualTo(savedQuestion.getContent());
      //고민인건, subject, content는 unique가 아닌데 중복일 경우에 대해서는 어떻게 처리하지?
    }

    @Test
    void testFindAll() {
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).size().isEqualTo(2);
    }

    @Test
    void testFindById() {
        Optional<Question> byId = questionRepository.findById(3);
        if(byId.isPresent()) {
            Question findQuestion = byId.get();
            assertThat(findQuestion.getSubject()).isEqualTo("첫번째 질문글");
        } else {
            log.error("해당되는 객체가 존재하지 않습니다.");
        }
    }

    /**
     * subject 필드의 경우 unique가 보장이 안됨
     * 내가 실수아닌 실수로 테스트코드 수행 후 삭제하는 (AfterEach)를 안하고 insert테스트를 두 번 돌려서 동일한 데이터값이 중복으로 들어갔음
     * 그래서 subject로 찾는걸 하니까 NonUniqueResultException 예외 발생!
     * JPA에서 찾아올 때 부터 예외가 발생하는구나! 쿼리만들 때 WHERE절에서 문제가 있나-> 반환타입때문에 그런가?
     * ->  Question findBySubject(String subject);
     * -> 반환타입을 List로 받아보면?
     *
     */
//    @Test
//    void testFindBySubject() {
//        Question findQuestion = questionRepository.findBySubject("첫번째 질문글");
//        log.info("findQuestion={}",findQuestion);
//    }

    @Test
    void testFindBySubject2() {
        List<Question> findQuestions = questionRepository.findBySubject("첫번째 질문글");

        assertThat(findQuestions).size().isEqualTo(2);
    }

    @Test
    void testFindBySubjectLike() {
        List<Question> bySubjectLike = questionRepository.findBySubjectLike("%질문글%");
        assertThat(bySubjectLike).size().isEqualTo(2);
    }

    /**
     * 테스트코드 실행하며 하이버네이트 쿼리에 update가 안보여서 의아했는데 'dirty checking'때문!
     * 그렇다면, gradle에서는 임의로 commit하고싶다면? -> gradle도 엔티티메니저 등록하고 생성해야됨 ㅇㅎ
     */
    @Test
    @DisplayName("데이터 수정 테스트")
    void testUpdate() {
        //given
        Optional<Question> opFindQuestion = questionRepository.findById(1);



        //when
        if (opFindQuestion.isPresent()) {
            Question question = opFindQuestion.get();
            question.setSubject("수정된 제목");
            questionRepository.save(question); //update쿼리가 있는게 아니라, 기존 객체에 pk로 판단해서 JPA가 수정 쿼리 날려주나봄
        }

        //then
        Optional<Question> byId = questionRepository.findById(1);
        assertThat(byId.get().getSubject()).isEqualTo("수정된 제목"); //테스트코드라 isPresent() 체크 생략

    }

    /**
     * fail test
     * 이런 흐름으로 하고싶었는데, 'entityManagerFactory' defined in class path resource 경로를 못잡아서
     * 계속 에러남, 나중에 해보기
     */
//    @Test
//    @DisplayName("트랜잭션 커밋 테스트")
//    void testTransactionCommit() {
//        //given
//        EntityTransaction tx = em.getTransaction();
//
//        Optional<Question> opFindQuestion = questionRepository.findById(1);
//
//
//        //when
//        if (opFindQuestion.isPresent()) {
//            Question question = opFindQuestion.get();
//            question.setSubject("수정된 제목");
//
//            //------------------------
//            tx.commit();
//            em.flush(); //영속 컨텍스트 비워주고
//
//            questionRepository.save(question); //update쿼리가 있는게 아니라, 기존 객체에 pk로 판단해서 JPA가 수정 쿼리 날려주나봄
//
//        }
//
//        //then
//        Optional<Question> byId = questionRepository.findById(1);
//        assertThat(byId.get().getSubject()).isEqualTo("수정된 제목"); //테스트코드라 isPresent() 체크 생략
//
//    }


    @Test
    void testJpa() {
        Optional<Question> oq = this.questionRepository.findById(1);
        assertTrue(oq.isPresent()); //if문대신
        Question q = oq.get();
        q.setSubject("update쿼리 확인할거야");
        this.questionRepository.save(q);
    }

    @Test
    void testDelete() {
        long nowQuestionCount = questionRepository.count();
        log.info("nowQuestionCount={}",nowQuestionCount);

        questionRepository.deleteById(1);
        assertThat(questionRepository.count()).isEqualTo(nowQuestionCount-1);
    }





}
