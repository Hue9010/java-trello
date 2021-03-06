package codesquad.web.api;

import static io.restassured.RestAssured.given;


import org.junit.Test;
import org.springframework.http.HttpStatus;

import codesquad.domain.Board;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.http.ContentType;
import support.test.AcceptanceTest;

public class ApiBoardsAcceptanceTest extends AcceptanceTest{
	
	@Test
	public void showBaord() {
		given()
			.auth()
			.preemptive()
			.basic("hue@korea.kr","password")
		.when()
			.get("/api/boards")
		.then()
			.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void showBaord_no_login() {
		given()
		.when()
			.get("/api/boards")
		.then()
			.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void createBoard() {
		Board boardDto = new Board("newBoard");
		given()
			.auth()
			.form("hue@korea.kr", "password", new FormAuthConfig("/login", "username", "password"))

//			.auth()
//			.preemptive()
//			.basic("hue@korea.kr","password")
			.contentType(ContentType.JSON)
			.body(boardDto)
		.when()
			.post("/api/boards")
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.extract().as(Board.class);
		
		//두개 이상 만들어도 문제 없는지 확인
		given()
			.auth()
			.form("hue@korea.kr", "password", new FormAuthConfig("/login", "username", "password"))
			.contentType(ContentType.JSON)
			.body(boardDto)
		.when()
			.post("/api/boards")
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.extract().as(Board.class);
	}
	
	@Test
	public void createBoard_no_login() {
		Board boardDto = new Board("newBoard");
		given()
			.contentType(ContentType.JSON)
			.body(boardDto)
		.when()
			.post("/api/boards")
		.then()
			.statusCode(HttpStatus.FORBIDDEN.value());
	}
}
