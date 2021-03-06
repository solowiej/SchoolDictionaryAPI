package com.restapi.rest;

import com.restapi.rest.model.Student;
import com.restapi.rest.model.dto.CreateStudentRequest;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class RestApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final String STUDENT_NAME = "TestName";
    private final String STUDENT_SURNAME = "TestSurname";
    private final boolean STUDENT_ALIVE = true;
    private final LocalDate STUDENT_DATE = LocalDate.of(2000, 1, 1);


    @Test
    public void test_1_add_student() {
        CreateStudentRequest request = new CreateStudentRequest(
                null,
                STUDENT_NAME,
                STUDENT_SURNAME,
                STUDENT_ALIVE,
                STUDENT_DATE);


        HttpEntity<CreateStudentRequest> httpEntity = new HttpEntity<>(request);

        ResponseEntity<Long> response = testRestTemplate
                .exchange("/student", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Long>() {
                });

        Long createdId = response.getBody();
        Assert.assertNotNull(createdId);
    }

    @Test
    public void test_2_check_added_student() {
        ResponseEntity<List<Student>> response = testRestTemplate
                .exchange("/student", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
                });

        List<Student> stringList = response.getBody();


        Assert.assertFalse(stringList.isEmpty());
        Assert.assertEquals(1, stringList.size());

        Student retrieved = stringList.get(0);


        Assert.assertEquals(STUDENT_NAME, retrieved.getName());
        Assert.assertEquals(STUDENT_SURNAME, retrieved.getSurname());
        Assert.assertEquals(STUDENT_ALIVE, retrieved.getIsAlive());
        Assert.assertEquals(STUDENT_DATE, retrieved.getDateOfBirth());

        Integer ageSinceBirthYear = LocalDate.now().getYear() - STUDENT_DATE.getYear();

        Assert.assertEquals(ageSinceBirthYear, retrieved.getAge());


    }

    @Test
    public void test_3_check_deleted_student() {
        ResponseEntity<List<Student>> getResponse = testRestTemplate
                .exchange("/student", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
                });

        List<Student> responseGetBody = getResponse.getBody();

        assert responseGetBody != null;
        Student student = responseGetBody.get(0);

        ResponseEntity<List<Student>> deleteResponse = testRestTemplate
                .exchange("/student/" + student.getId(), HttpMethod.DELETE, null, new ParameterizedTypeReference<List<Student>>() {
                });

        List<Student> responseDeleteBody = deleteResponse.getBody();
        Assert.assertNull(responseDeleteBody);
    }
}
