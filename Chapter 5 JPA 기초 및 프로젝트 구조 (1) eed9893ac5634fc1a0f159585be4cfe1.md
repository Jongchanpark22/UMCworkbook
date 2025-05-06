# Chapter 5. JPA 기초 및 프로젝트 구조 (1)

# 📝 학습 목표

---

1. JPA의 개념, 필요성에 대해 이해한다.
2. Spring data JPA를 이용하여 Entity 설계, 매핑을 한다.

# 📸 잠깐 ! 스터디 인증샷은 찍으셨나요?📸

---

* 스터디리더께서 대표로 매 주차마다 한 장 남겨주시면 좋겠습니다!🙆💗
 (사진을 저장해서 이미지 임베드를 하셔도 좋고, 복사+붙여넣기해서 넣어주셔도 좋습니다!)

[](https://www.notion.so)

# 📑 5주차 주제

---

이제 본격적으로 Springboot를 사용할 시간입니다! 👏

이번 5주차 실습에서는 우선적으로 설계한 DB의 ERD를 통해, 오늘 실습은 **Spring Data JPA를 이용하여 Entity를 설계하는 것**이 필요합니다.

![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled.png)

저는 위의 ERD로, 여러분은 여러분이 설계했던 ERD를 가지고 JPA 엔티티 매핑을 해봅시다.

본격적인 실습에 들어가기 전에, **JPA는 또 뭐고 Spring Data JPA는 또 뭔지🧐**에 대해 이론적으로 알아보는 시간을 가지겠습니다 🪄 

## 객체 지향 언어와 관계형 데이터베이스

자바는 객체 지향 언어(Object-Oriented Language) 라고 합니다. 
지난 주차 때, 스프링은 자바 기반 프레임워크라고 했었죠?

그러나, 우리는 데이터를 관리할 때 관계형 데이터베이스(RDBMS)를 많이 사용합니다.

지난 4주차 실습 때에도 저희는 데이터베이스로 RDBMS 중 하나인 MySQL을 사용했습니다.

자, 문제는 여기서 시작합니다.

### 객체 지향 언어 ≠ RDB 패러다임의 불일치

**객체 지향적 언어인 자바**는 궁극적으로 **캡슐화/상속/다형성을 활용하는 것이 목표**이고, 

**RDBMS(관계형 데이터베이스)**는 **데이터를 정교하게 구성하는 것이 목표**입니다.

서로 이루고자 하는 바가 명확하게 다르기에, 딱 보기에도 둘의 조합이 완전하게 들어맞지는 않겠죠?

네‼️

*CRUD 쿼리를 다 짜야하고, Java 코드를 SQL로 바꾸고 SQL 쿼리를 Java로 바꾸어야 하니 테이블을 하나 만들거나 변경될 때마다 수십 개의 CRUD 쿼리를 개발자가 직접 짜야 합니다. 🤷*

![*내가 개발자인지 SQL Mapper인지 모르겠는 혼란한 상황 발생*🥲🥲](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/WorriedGIF.gif)

*내가 개발자인지 SQL Mapper인지 모르겠는 혼란한 상황 발생*🥲🥲

예를 들어볼까요?

순수 JDBC 코드로 회원가입을 진행하는 코드를 작성해보겠습니다.

회원가입을 위해서는

1. 기존의 이미 등록된 회원(중복회원)인지 체크해주기
2. 1에 위반되지 않다면 회원가입 진행

이란 2가지 단계로 크게 나눠볼 수 있는데요, 이에 따라서 각각 1번에서는 조회(SELECT문), 삽입(INSERT문)에 해당하는 SQL 쿼리를 작성하고, 맵핑해주는 작업이 필요합니다.

```java
...(중략)...
			// 중복 회원인지 먼저 확인해주는 작업
			pStmt = conn.prepareStatement("SELECT * from member WHERE id = ?");
			pStmt.setLong(1, newUser.getId());
			res = pStmt.executeQuery();
			
			if(res.next()) {
				return "이미 가입한 사용자입니다. 다른 id를 사용해주세요.";
			}
			JdbcUtil.close(res);
			JdbcUtil.close(pStmt);
			
			
			String sql = "INSERT INTO member(id, password, major, email, name, phone_num) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			pStmt = conn.prepareStatement(sql);
			pStmt.setLong(1, newUser.getId());
			pStmt.setString(2, newUser.getPassword());
			pStmt.setString(3, newUser.getMajor());
			pStmt.setString(4, newUser.getEmail());
			pStmt.setString(5, newUser.getName());
			pStmt.setString(6, newUser.getPhoneNum());
			
			int rowsAffected = pStmt.executeUpdate();
			if(rowsAffected > 0) {
				conn.commit();
				String welcomeName = newUser.getName();
				return welcomeName+"님, 회원가입에 성공하였습니다.";
			}
...(중략)...
```

굉장히 복잡하고 보기에 힘들다는 걸 알 수 있습니다.

개발자가 일일히 SQL 쿼리를 짜고, 맵핑해주고 하는 작업이 여간 번거로운 게 아니죠.

그리고 사실 어떤 비즈니스 프로젝트에서 필수 로직이라고 할 수 있는 회원 관련 관리나 기본적인 API들은 사실 쿼리로 따지자면 **반복적인 CRUD 작업**이 굉장히 많습니다.

반복적인 걸 계속하면 지루하듯이

따라서 이런 반복적인 CRUD 작업을 획기적으로 줄일 수 없을까? 란 생각에서 비롯되어 나온 기술이 바로 **JPA**입니다.

## JPA란?

JPA(Java Persistence API)란, **자바 진영에서 ORM(Object-Relational Mapping) 기술의 표준으로 사용되는 인터페이스의 모음**입니다.

### **ORM(Object Relational Mapping) 기술이란? 🌟**

우리의 애플리케이션의 클래스와 RDB(Relational DataBase)의 테이블을 매핑(연결)한다는 뜻입니다.

기술적인 측면에서는 애플리케이션의 객체를 RDB 클래스에 자동으로 연결시켜준다고 보시면 됩니다.

즉, **❤️‍🔥반복적인 CRUD SQL 쿼리문을 굳이 짤 필요 없이 자동으로 매핑하여 날려준다❤️‍🔥**고 생각하시면 됩니다👍👍 

즉, ‘인터페이스’인 만큼 실제적으로 구현된 것이 아니라, 개발자가 구현한 클래스와 매핑을 해주기 위해 사용되는 프레임워크입니다.

JPA를 구현한 대표적인 오픈소스로는 **`Hibernate`** 가 있습니다.

# Spring 프로젝트 설계 및 구조

## Springboot 디렉토리 컨벤션

코드 스타일, 디렉토리 컨벤션은 프로젝트마다 제각각이라
**제가 제공하는 방식이 정답이 아님을 명심**해주세요!

우선 아래와 같은 형태로 package를 만들어주세요

![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%201.png)

이 패키지들은 추후 더 추가가 될 것이며, 12주차에는 github action을 위한 디렉토리도 생깁니다!

## 패키지 소개

### domain 패키지

**JPA에서 사용하기 위한 엔티티 클래스를 저장**하기 위한 패키지입니다.

### controller 패키지

**http 요청이 오면, 그에 대한 응답을 주는 클래스의 모임**이며,

**응답을 주기 위한 과정들을** **service에서 처리**하도록 ****합니다.

### service 패키지

**비즈니스 로직이 필요한 클래스들의 모임**이며 가장 복잡한 코드가 들어갑니다.

**contoller에서 service의 메소드를 호출**하게 되며,

**service는 repository의 메소드를 호출**하게 됩니다.

### repository 패키지

database와 통신을 하는 계층으로,

Spring Data JPA를 이용해서 만든 repository를 이용할 것입니다.

### dto 패키지

**클라이언트가 body에 담아서 보내는 데이터를 받기 위한 클래스.**

혹은 **Database에서 받아온 데이터를 클라이언트에게 보여주기 위한 클래스**입니다.

Database에서 받아온 데이터(엔티티)를 그대로 응답으로 주게 되면 문제가 생기게 됩니다.

요구사항의 변경이 생겨 Database table의 설계가 바뀌게 되어 엔티티의 변경이 생겼을 시,
엔티티를 그대로 응답으로 줄 경우 Database의 변경사항이 프론트엔드 개발자에게까지 영향을 주게 됩니다.

이는 당연히 좋지 않은 설계로,

**dto를 통해 응답 데이터를 결정하게 되면, Database의 변경이 생길 경우 dto만 변경하면 되기에** 더 좋은 설계가 됩니다.

### converter 패키지

converter는 데이터 형식 간의 변환을 수행하는 역할입니다.
그렇다면 entity to dto를 해야 한다는 것인데, 이것은 어디서 할까요?

**repository에서 받아온 엔티티를 dto로 바꾸는 과정**을 converter에서 하게 됩니다.

**추가로 entity의 생성 역시** converter에서 수행하기도 합니다.

converter에서 entity의 생성을 하지 않고 service에서 하도록 하는 경우도 있습니다.

converter에서 엔티티의 생성을 하게 되면, service는 순수하게 비즈니스 로직에만 집중할 수 있어 단일 책임 원칙 측면에서 더 좋습니다.

저 같은 경우는 converter에서 entity를 생성하도록 합니다.

## converter의 사용 위치

### 1. service에서 dto 생성

> **service에서 converter를 통해 dto를 controller에게 리턴** 해주는 것.
controller에서는 그저 dto를 응답으로 주기만 해도 되고,
> 

### 2. controller에서 dto 생성

> service에서 entity를 리턴하고
> 
> 
> **controller에서 converter를 통해 dto를 만들어서** **응답**을 주기도 합니다.
> 

각각은 장단점이 존재합니다.

우선 전자의 경우 controller에 엔티티가 노출되지 않아 보안적인 측면에서 도움이 됩니다.

대규모 프로젝트의 경우 한명이 모든 api의 컨트롤러, 서비스, 리포지토리를 모두 작업하지 않고

각자 따로 작업을 하는 경우가 있습니다.

이렇게 엔티티에 민감한 정보가 있는 경우, 권한이 있는 개발자만 엔티티에 담긴 데이터를 보고, dto로 데이터를 걸러서 컨트롤러로 리턴을 하여

컨트롤러를 작업하는 사람으로 하여금 민감한 정보를 볼 수 없게 할 수 있습니다.

후자의 경우는 service의 함수가 범용성이 커져 유지보수가 편해집니다.

동일한 엔티티를 필요로 하는 여러 컨트롤러, 혹은 validation을 위한 어노테이션(9주차에서 등장)에서 서비스의 함수를 재활용 할 수 있어 코딩이 더 편해집니다.

둘 중에 무엇을 해야 하는가,

***정답은 없습니다.***

저 같은 경우는 보통 후자를 주로 사용합니다.

상황에 따라 서비스에서 엔티티를 반환할 수 없는 경우가 자주 생깁니다.
이런 경우는 dto를 반환하도록 합니다!

저의 경우, 컨버터에서 엔티티 생성을 하기 때문에 web 패키지 내부에 두지 않았습니다.

만약 컨버터를 오로지 dto를 만드는 역할만 하게 한다면, web 패키지 내부에 둬도 좋습니다.

## Entity 매핑

**이번 주차는 domain 패키지에서 entity 매핑을 하는 것에 집중합시다!**

### DB 준비

1. **RDS 혹은 로컬에서 DB 준비하기**
    
    RDS가 아무 생각없이 내버려 두면 요금 폭탄을 맞을 수 있습니다.
    
    잘 관리할 자신이 있으신 분들은 RDS에 데이터베이스를 만드시고, 아닌 경우는 로컬에 만들어 주세용!
    
    저는 RDS를 쓰겠습니다.
    
    DB 이름은 여러분 맘대로 하셔도 됩니다. 저는 study로 할게용.
    
    우선 Datagrip으로 RDS 혹은 로컬 mysql에 접속하여 DB를 만들어 줍시다.
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%202.png)
    
    1. **application.yml 설정하기**
        
        기존에 있던 파일을 없애고 확장자가 yml인 것을 쓰는 것이 더 편합니다
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%203.png)
        
        yml 파일에 아래처럼 작성해주세요
        
        (RDS 혹은 로컬인지에 따라 yml 파일 작성 내용이 다르니 코드 아래 캡션을 참고해주세요!)
        
        ```java
        spring:
          datasource: 
            url: jdbc:mysql://localhost:3306/study
            username: 님들의 유저 이름
            password: 님들의 비밀번호
            driver-class-name: com.mysql.cj.jdbc.Driver
          sql:
            init:
              mode: never
          jpa:
            properties:
              hibernate:
                dialect: org.hibernate.dialect.MySQL8Dialect
                show_sql: true
                format_sql: true
                use_sql_comments: true
                hbm2ddl:
                  auto: update
                default_batch_fetch_size: 1000
        ```
        
        **url 부분의 경우, 아래 url을 그대로 쓰는게 아니라 여러분들의 RDS에 맞춰서 넣으세용**
        
        ```java
        spring:
          datasource:
            url: jdbc:mysql://zipdabang-db.claedpluhwyw.ap-northeast-2.rds.amazonaws.com:3306/study
            username: 님들의 rds 루트 유저 이름
            password: 님들의 rds 비밀번호
            driver-class-name: com.mysql.cj.jdbc.Driver
          sql:
            init:
              mode: never
          jpa:
            properties:
              hibernate:
                dialect: org.hibernate.dialect.MySQL8Dialect
                show_sql: true
                format_sql: true
                use_sql_comments: true
                hbm2ddl:
                  auto: update
                default_batch_fetch_size: 1000
        ```
        
        이제 application.yml에 DB 접속 정보를 넣었으니,
        의존성을 아래처럼 주석 처리 했던 것을 없애주세요.
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%204.png)
        

### domain 패키지에서 엔티티 만들기

<aside>
🚨 **JPA의 내부 원리는 따로 학습 해주세요!**

</aside>

1. **Member 엔티티**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }
    ```
    
    우선 기본 키인 id만 만들어 줬습니다.
    
    기본 키를 만드는 방법에는 여러가지가 있지만,
    
    가장 편리한 **@GeneratedValue(strategy = GenerationType.IDENTITY) 방법**을 사용하겠습니다.
    
    해당 내용은 **JPA가 통신을 하는 DBMS의 방식을 따른다는 뜻**입니다.
    
    우리는 MySQL을 사용하니, MySQL을 따르게 됩니다.
    
    - **@Entity 어노테이션을 통해 해당 클래스가 JPA의 엔티티임을 명시**합니다.
    - **@Getter**는 lombok에서 제공해주는 것으로, getter를 만들어주는 어노테이션입니다.
    (자바에서 getter가 무엇인지는 다들 아실거라고 믿습니다. 🙂)
    - **@Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor**
        
        위 세 개의 어노테이션은 자바의 디자인 패턴 중 하나인 ***빌더 패턴***을 사용하기 위함입니다.
        
        빌더 패턴을 사용하면 생성자를 사용하는 것보다 더욱 편리하게 코딩이 가능합니다!
        
        더 자세한 빌더 패턴의 사용 이점은 아래 링크를 참고해주세요.
        
        - 자바 빌더 패턴 참고
            
            [[Java] 빌더 패턴(Builder Pattern)을 사용해야 하는 이유](https://mangkyu.tistory.com/163)
            
            [[생성 패턴] 빌더 패턴(Builder pattern) 이해 및 예제](https://readystory.tistory.com/121)
            
            [💠 빌더(Builder) 패턴 - 완벽 마스터하기](https://inpa.tistory.com/entry/GOF-💠-빌더Builder-패턴-끝판왕-정리)
            
    
    이제 멤버 테이블에 해당하는 엔티티를 이어서 만들어 봅시다.
    
    ![멤버 테이블](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%205.png)
    
    멤버 테이블
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    
        private String address;
    
        private String specAddress;
    
        private LocalDate inactiveDate;
    
        private String email;
    
        private Integer point;
    }
    ```
    
    위 코드를 보면 테이블에서는 spec_address인데 저기서는 specAddress입니다.
    
    그냥 순수 JPA와 달리 Spring data JPA를 사용하면, 실제 DB에 적용 시 specAddress를 spec_address 이렇게 바꿔줍니다!
    
    그러니 평소 자바 코딩을 하는 것처럼 편하게 코딩하면 됩니다.
    
    이제 더 고민을 해야하는 요소가 있는데. 그것을 한 번 고민해보도록 하죠.
    
    **gender, status, social_type**
    
    이 3가지 요소는 name, email처럼 어떤 값이 저장될지 모르는 것이 아니라
    
    **정해진 값들 중에 특정 값이 저장이 됩니다.**
    
    이런 값들은 타입을 String으로 하기 보다는 **enum을 사용하는 것이 좋습니다.**
    

1. **enum 만들기**
    - 아래 사진처럼 enums 패키지를 domain 아래에 만들어 주세요.
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%206.png)
        
    - 이제 이렇게 enum을 만들어주세요
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%207.png)
        
    - 아래와 같이 미리 정해진 값들을 저장해줍니다.
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%208.png)
        
        ***enum 역시도 클래스이기에, 생성자를 두는 등 여러 방식으로 사용이 가능합니다.
        하지만 이 내용은 다음 주차의 에러 핸들링과 API 응답 통일 과정에서 다룹니다.***
        
    - 이제 social_type과 status도 enum을 만들어줍시다.
        
        ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%209.png)
        
        ***당연히 위는 그저 예시일 뿐, 실제로는 요구 사항에 맞게 해주세요.***
        
        MemberStatus는 Mission에도 Status가 있어서 구분을 위해 이름을 저렇게 지었습니다.
        
        이제 enum을 만들었으니 Member 엔티티에 적용 해봅시다.
        
2. **Member 엔티티 적용**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    
        private String address;
    
        private String specAddress;
    
        @Enumerated(EnumType.STRING)
        private Gender gender;
        
        @Enumerated(EnumType.STRING)
        private SocialType socialType;
        
    		@Enumerated(EnumType.STRING)
        private MemberStatus status;
    
        private LocalDate inactiveDate;
    
        private String email;
    
        private Integer point;
    }
    ```
    
       @Enumerated(EnumType.STRING)
        private Gender gender;
        
        @Enumerated(EnumType.STRING)
        private SocialType socialType;
        
        @Enumerated(EnumType.STRING)
        private Status status;
    
    **@Enumerated 어노테이션을 이용해서 enum을 entity에 적용할 수 있습니다.**
    
    이 때, 반드시 **EnumType을 STRING**으로 해주세요!
    
    기본 값인 ORDINAL을 사용하면 데이터베이스에 enum의 순서가 저장이 되는데,
    
    만약 Springboot에서 enum의 순서를 바꾸게 될 경우 에러가 생기게 됩니다.
    
    따라서 반드시 STRING을 사용해주세요!
    

연관 관계는 일단 미루고 엔티티 자체를 우선 완성해봅시다.

**단, N : M 관계에서 매핑 테이블을 제외하고 만들어봅시다.**

그리고 마찬가지로 **Created_at과 Updated_at도 제외**하고 만들어주세요!

![ERD](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2010.png)

ERD

- 예시
    
    ### food_category
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class foodCategory {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    
    }
    ```
    
    ### terms
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Terms {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String title;
    
        private String body;
    
        private Boolean optional;
    }
    ```
    
    ### review
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Review {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String title;
    
        private Float score;
    }
    ```
    
    ### region
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Region {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    }
    ```
    
    ### store
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Store {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    
        private String address;
    
        private Float score;
    }
    ```
    
    ### mission
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Mission {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private Integer reward;
    
        private LocalDate deadline;
    
        private String missionSpec;
    }
    ```
    

그러면 Created_at, Updated_at은 어떻게 할까요?

둘은 모든 엔티티에서 다 사용하므로 매번 넣어주기가 매우 귀찮습니다.

### Created_at, Updated_at

- 아래처럼 base라는 패키지를 만들어주세요
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2011.png)
    

- BaseEntity는 abstract Class로 만들어주세요
    
    ```java
    @MappedSuperclass
    @EntityListeners(AuditingEntityListener.class)
    @Getter
    public abstract class BaseEntity {
    
        @CreatedDate
        private LocalDateTime createdAt;
    
        @LastModifiedDate
        private LocalDateTime updatedAt;
    }
    ```
    

- 이제 모든 엔티티 클래스마다 위의 클래스를 **상속** 받으면 됩니다.
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2012.png)
    

- 그리고 Spring boot Application 자체도 `JpaAuditing`사용이 가능하도록 변경을 해줘야 합니다.
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2013.png)
    
    아래 코드처럼 변경 해주세요
    
    ```java
    @SpringBootApplication
    @EnableJpaAuditing
    public class StudyApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(StudyApplication.class, args);
    	}
    
    }
    ```
    

### 매핑 테이블의 설계

매핑 테이블은 따로 패키지에 모아서 관리하는 것이 보기에 편합니다.

- 아래처럼 mapping 패키지를 만들어주세요.
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2014.png)
    

- 아래처럼 남은 매핑 테이블 엔티티 클래스를 추가해주세요
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2015.png)
    

- 이제 매핑 테이블도 연관관계를 제외한 나머지 정보를 멤버변수로 만들어줍시다.
    
    ### MemberAgree
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class MemberAgree extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
    }
    ```
    
    ### MemberMission
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class MemberMission extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @Enumerated(EnumType.STRING)
        private MissionStatus status;
    }
    ```
    
    ### MissionStatus enum
    
    ![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2016.png)
    
    ### MemberPrefer
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class MemberPrefer extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
    }
    ```
    

### 마지막, 연관 관계 매핑

연관 관계 매핑은 단방향 매핑, 그리고 양방향 매핑이 있습니다.

**양방향 매핑**을 사용하게 될 경우 버그가 생길 위험이 있지만,
양방향 매핑으로 인한 객체 그래프 탐색은 프로그래밍을 굉장히 편리하게 해줍니다.

우선 **연관 관계 주인**이 무엇인지 알아봅시다.

**연관 관계 주인**이란, **실제 데이터베이스에서 외래키를 가지는 엔티티(테이블)를 의미**합니다.

**단방향 매핑이란 연관 관계 주인에게만 연관 관계를 주입하는 것**이고

**양방향 매핑은 연관 관계 주인이 아닌 엔티티에게도 연관 관계를 주입하는 것**입니다.

양방향 매핑의 이점은 9주차에서 확인 할 수 있습니다.

우선 단방향 매핑부터 해보겠습니다.

![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2017.png)

member_prefer 테이블의 경우 member와 food_category 모두에 대한 외래키를 가지고 있습니다.

- **1 : N**의 경우 **N에 해당하는 테이블이 외래키를 가지며** **N에 해당하는 엔티티가 연관 관계의 주인**이죠.
- **1 : 1**의 경우는 둘 중 하나만 외래키를 가지면 되기에 **원하는 엔티티를 연관 관계 주인으로 설정**하면 됩니다.

member_prefer 엔티티에 연관 관계 매핑을 해봅시다.

```java
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberPrefer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FoodCategory foodCategory;

}
```

N : 1에서 N에 해당하는 엔티티가 1에 해당하는 엔티티와 연관 관계를 매핑할 때

**@ManytoOne 어노테이션**을 씁니다.

(fetch = FetchType.LAZY)는 ***지연 로딩을 설정하는 것입니다.***

즉시 로딩과 지연 로딩의 차이점에 대해서는 6주차 때 <Spring  Data JPA 영속성 컨텍스트> 내용을 하며 다시 나올테니 그때 참고해주시면 됩니다 ‼️☺️

@JoinColumn은 실제 데이터베이스에서 해당 칼럼(외래키)의 이름을 설정하는 것입니다.

이제 모든 엔티티에 단방향 연관 관계를 설정하겠습니다.

- **MemberAgree**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class MemberAgree extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id")
        private Member member;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "terms_id")
        private Terms terms;
    }
    ```
    
- **Mission**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Mission extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private Integer reward;
    
        private LocalDate deadline;
    
        private String missionSpec;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "store_id")
        private Store store;
    }
    ```
    

## 양방향 매핑

양방향 매핑의 장점은 **객체 그래프 탐색으로 인한 이점**이 있으며,

필수적인 기능인 **cascade의 설정이 가능하다는 점**입니다.

원래 database에서의 cascade는 본래 외래키를 가진, **연관 관계의 주인인 테이블에 설정**을 해서

**참조 대상인 테이블의 칼럼이 삭제 될 때 같이 삭제되거나 변경이 될 때 같이 변경이 되는 기능**입니다.

그러나 JPA에서는 반대로 연관관계의 주인이 아닌, **참조의 대상이 되는 엔티티에 설정을 해야합니다.**

이는, 단방향 매핑 만으로는 cascade 설정을 하는 것이 문제가 있다는 것입니다.

만약 단방향 매핑만 적용이 된 상태에서 cascade를 설정 시,

member를 참조하는 **review가 삭제가 될 때 member도 같이 삭제가 되는 끔찍한 상황이 발생합니다.**

따라서 양방향 매핑이 있어야 올바르게 cascade의 적용이 가능합니다.

단, 양방향 매핑을 할 경우 연관 관계 편의 메서드가 필요하게 됩니다.

**연관 관계 편의 메서드가 무엇이고 왜 필요한지도 스스로 찾아보시길 바랍니다!**

이제 Member 엔티티에 양방향 매핑을 해보겠습니다.

### Member 양방향 매핑

```java
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String specAddress;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDate inactiveDate;

    private String email;

    private Integer point;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAgree> memberAgreeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberPrefer> memberPreferList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberMission> memberMissionList = new ArrayList<>();
}
```

양방향 매핑의 경우 1 : N에서 1에 해당하는 엔티티에게 설정합니다.

**@OneToMany 어노테이션**으로

**1에 해당하는 엔티티가 N에 해당하는 엔티티와 관계가 있음을 명시**하며,
이 때, **N에 해당하는 엔티티에서 ManyToOne이 설정 된 멤버변수를 mappedBy**를 합니다.

**CascadeType.ALL**이란 Member의 변화에 따라 Review, MemberPrefer 등의 엔티티가 영향을 받는다는 것을 의미합니다.

이렇게 하면, 멤버가 삭제 될 때, 멤버를 참조하는 나머지 데이터도 같이 삭제가 됩니다.

<aside>
⚠️ ***만약 이렇게 하지 않으면, 나중에 멤버 삭제 시,
연결된 데이터들을 하나 하나 다 삭제를 해야 해서 매우 짜증납니다.***

</aside>

그리고 멤버가 적은 게시글을 멤버가 탈퇴 시 삭제를 할지 말지도 기획자와 논의를 해서 결정을 해야합니다. 에브리타임만 봐도 탈퇴한다고 게시글이 사라지는게 아니잖아요?

**따라서 여러분은 협업의 관점에서
늘 PM과 프론트 개발자와 소통을 할 것을 염두하고 작업을 해야 합니다!**

이제 남은 나머지 엔티티에도 양방향 매핑을 적용합시다. 

(역시 전부 하지는 않습니다. 테이블 2개만 보여드리고 나머지는 미션으로 해주세요!)

**음식 카테고리는 보통 수정/삭제를 잘 하지 않으므로 양방향 매핑을 하지 않을게요.**

- **Terms**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Terms extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String title;
    
        private String body;
    
        private Boolean optional;
    
        @OneToMany(mappedBy = "terms", cascade = CascadeType.ALL)
        private List<MemberAgree> memberAgreeList = new ArrayList<>();
    }
    ```
    
- **Mission**
    
    ```java
    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class Mission extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private Integer reward;
    
        private LocalDate deadline;
    
        private String missionSpec;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "store_id")
        private Store store;
    
        @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
        private List<MemberMission> memberMissionList = new ArrayList<>();
    }
    ```
    

## 칼럼 별 세부적인 설정

이제 엔티티의 각 멤버 변수 별로 세부적인 설정을 해야 합니다.

단적인 예로 Member에서 name이 table에서는 varchar(20)인데, JPA에서 String으로 되어있습니다.

이렇게 되면 실제 데이터베이스에 varchar(20)으로 들어가지 않겠죠?

따라서 각 칼럼 별로 **세부적인 설정**을 (유니크, 디폴트 값, null이 가능한지 등등) 해줘야 합니다.

JPA에서 칼럼에 대한 세부적인 설정은 **@Column**을 이용해서 합니다.

워크북에서는 Member 엔티티에만 세부 설정을 할 것이고,

**나머지 엔티티의 칼럼에 대한 세부 설정은 미션으로 남겨두겠습니다.**

### Member

```java
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 40)
    private String address;

    @Column(nullable = false, length = 40)
    private String specAddress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private MemberStatus status;

    private LocalDate inactiveDate;

    @Column(nullable = false, length = 50)
    private String email;

    private Integer point;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAgree> memberAgreeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberPrefer> memberPreferList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberMission> memberMissionList = new ArrayList<>();
}
```

**columnDefinition = "VARCHAR(10)"** 이렇게 **칼럼의 타입을 직접 지정**할 수도 있고,

**@Column(nullable = false, length = 40)** 이렇게도 가능합니다.

gender와 status는 enum으로 되어있어서

**@Column(columnDefinition = "VARCHAR(10)")** 이렇게 설정을 했습니다.

주의할 점은 

@Column(columnDefinition = "VARCHAR(15) DEFAULT **'ACTIVE'**")

여긴데, **ACTIVE가 아니라 ‘ACTIVE’** 입니다.

그냥 ACTIVE로 하면

> create table member (
id bigint not null auto_increment,
created_at datetime(6),
updated_at datetime(6),
address varchar(40) not null,
email varchar(50) not null,
gender VARCHAR(10),
inactive_date date,
name varchar(20) not null,
point integer,
social_type varchar(255),
spec_address varchar(40) not null,
**status VARCHAR(15) DEFAULT ACTIVE,**
primary key (id)
)
> 

이렇게 DDL을 날려서 **오류가 나서 터집니다. 😱**

***mysql은 문자열을 무조건  ‘ ’로 감싸야 합니다.***

칼럼의 default 값은 `@ColumnDefault('ACTIVE')`
위 처럼도 가능합니다.

**역시나 문자열은 ‘’ 로 감싸야지 안 터집니다. ~~극혐🤮~~**

이제 엔티티를 모두 작성했으니,

application.yml에서 auto를 create로 하고 Spring boot를 실행하면

JPA가 @Entity가 붙은 대상을 찾아내어 Database에 테이블을 만들게 됩니다.

```java
jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        show_sql: true
        format_sql: true
        use_sql_comments: true
        hbm2ddl:
          **auto: create <- 요거**
        default_batch_fetch_size: 1000
```

주의할 점은 **기존 테이블을 모두 없애버리고 새로 만들게 되니**

처음 Database를 만드는 것 아니라면 **update**로 해두는 것이 좋습니다.

```java
jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        show_sql: true
        format_sql: true
        use_sql_comments: true
        hbm2ddl:
          **auto: update <- 이렇게**
        default_batch_fetch_size: 1000
```

update는 바뀐 부분에 대해서만 적용을 해줍니다!

**~~물론 데모데이 직전에 팀원들에게 엿을 멕이고 싶다면~~**

**~~전날에 create로 두고 서버를 실행시켜도 됩니다.~~**

create로 두고 처음으로 실행 시 뭔 에러가 생길텐데

우선 datagrip으로 가서 테이블이 생겼나 봅시다.

![Untitled](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/Untitled%2018.png)

저렇게 테이블이 다 생겼는데 오류가 생긴 것은

아까 create로 두면 기존 테이블을 없앤다고 했는데

**기존 테이블이 없는데 삭제하려고 하다 보니** 생긴 오류라서 그냥 무시하면 됩니다.

그냥 다시 한번 실행하면 오류 없이 테이블이 생길 것입니다.

❓ **어? 테이블 없음** ❓

**네, 망한 것입니다. 오타가 없나 찾아보세요**

# 🎯 핵심 키워드

---

<aside>
💡 주요 내용들에 대해 조사해보고, 자신만의 생각을 통해 정리해보세요!
레퍼런스를 참고하여 정의, 속성, 장단점 등을 적어주셔도 됩니다.
조사는 공식 홈페이지 **Best**, 블로그(최신 날짜) **Not Bad**

</aside>

- Domain
    
    소프트웨어 아키택처 상에서 비즈니스 개념이나 핵샘 데이터 구조를 표현하는 계층 또는 클래스를 의미한다. 핵심적인 데이터를 표현하느 클래스이고 데이터베이스와 매핑되는 경우가 많다. @entity 로 선언되고 db테이블과 매핑되는 객체를 의미한다.
    
- 양방향 매핑
    
    관계를 양쪽에서 탐색할 수 있는 방식으로두 엔티티간의 연결을 나타낸다. 양쪽에서 관계를 탐색하고 관련문제를 관리해야할 경우 양방향 매핑이 유리하다.
    
- N + 1 문제
    
    ORM 기술에서 특정 객체를 대상으로 수행한 쿼리가 해당 객체가가지고 있는 연관관계를 또한 조회하게 되면서 N번의 추가적인쿼리가 발생하게 되는 문제를 말한다. 근본적인 원인은 관계형 데이터베이스와 객체지향 언어 간의 패러다임차이로 인해 발생하게 된다.
    

# 📢 학습 후기

---

- 이번 주차 워크북을 해결해보면서 어땠는지 회고해봅시다.
- 핵심 키워드에 대해 완벽하게 이해했는지? 혹시 이해가 안 되는 부분은 뭐였는지?

<aside>
💡

</aside>

# ⚠️ 스터디 진행 방법

---

1. 스터디를 진행하기 전, 워크북 내용들을 모두 채우고 스터디에서는 서로 모르는 내용들을 공유해주세요.
2. 미션은 워크북 내용들을 모두 완료하고 나서 스터디 전/후로 진행해보세요.
3. 다음주 스터디를 진행하기 전, 지난주 미션을 서로 공유해서 상호 피드백을 진행하시면 됩니다.

# 🔥 미션

---

워크북을 따라해보고

1. 본인이 만들었던 ERD에 해당하는 테이블들에 대한 엔티티를 만들고
2. 워크북에서 다루지 않은 연관관계 매핑(양방향 포함)을 다 적용하고
3. 엔티티의 칼럼에 대한 세부적인 설정을 모두 하여
4. 로컬 디비에 실제로 테이블이 생긴 것을 캡쳐할 것(by datagrip)
5. 이후 본인의 깃허브 리포지토리를 만들어 mission5 브랜치에 올린 후,
**해당 깃허브 링크를 본인 워크북에 포함해오기. (미션 기록란에 링크 제출)**
    
    **❗main 브랜치에 올리지 말 것!** (브랜치 명이 굳이 mission5 이 아니어도 됨!) **❗**
    

[시니어 미션](https://www.notion.so/1d5b57f4596b81abb3c0c6f0c4c4637a?pvs=21)

<aside>
📌 **주의 사항**

결과물만 올리면 안되고, 중간 과정 또한 기록을 남겨주셔야 합니다.
(DB 연결, 테이블 생성 등)

**결과물과 중간 과정 모두 기록하여 제출**하라는 뜻입니다!

</aside>

# 💪 미션 기록

---

<aside>
🍀 미션 기록의 경우, 아래 미션 기록 토글 속에 작성하시거나, 페이지를 새로 생성하여 해당 페이지에 기록하여도 좋습니다!

하지만, 결과물만 올리는 것이 아닌, **중간 과정 모두 기록하셔야 한다는 점!** 잊지 말아주세요.

</aside>

- **미션 기록**
    
    ![storevisit.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/storevisit.png)
    
- 1주차에 만든 ERD 테이블을 기준으로 만들었습니다.

![스크린샷 2025-05-07 오전 12.32.32.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.32.32.png)

- 경로는 다음과 같이 mapping 따로 Entity 클래스를 따로 만들었고 enums도 별개로 구성하였습니다. StudyApplication 을 실행하는데 있어 하위파일의 엔티티를 읽어올수 없는 문제가 발생해 경로를 하나의 패키지 않으로 설정했습니다.

![스크린샷 2025-05-07 오전 12.32.41.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.32.41.png)

- 데이터베이스와의 연결을 위해 다음과 같이 Application.yml 파일을 만들었고 현 상태는 create로 테이블을 생성한 상태입니다.

![스크린샷 2025-05-07 오전 12.32.53.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.32.53.png)

- created_at, upload_at 을 구현하기위해 다음과 같이 BaseEntity 를 구성하였습니다.

![스크린샷 2025-05-07 오전 12.33.18.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.33.18.png)

- 매핑테이블중 하나인 테이블로 필요한 컬럼들을 만들어주고 양쪽을 단방향 매핑으로 연동하여 가운데서 매핑테이블의 역할을 하도록 하였습니다.

![스크린샷 2025-05-07 오전 12.33.43.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.33.43.png)

- 회원테이블로 가장 많은 컬럼을 가지고 있는 테이블로 세부적인 컬럼 디테일을 추가했고 필요한 정보에 대해 양방향 매핑하였습니다.

![스크린샷 2025-05-07 오전 12.34.05.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.34.05.png)

- 가게 테이블과 리뷰테이블에서 양방향 매핑을 구현했습니다.

![스크린샷 2025-05-07 오전 12.34.43.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.34.43.png)

실행 파일로 필요한 라이브러리를 참조해주고 JPA 설정을 해주었습니다.

![스크린샷 2025-05-07 오전 12.46.05.png](Chapter%205%20JPA%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%8C%E1%85%A6%E1%86%A8%E1%84%90%E1%85%B3%20%E1%84%80%E1%85%AE%E1%84%8C%E1%85%A9%20(1)%20eed9893ac5634fc1a0f159585be4cfe1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-05-07_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_12.46.05.png)

- 결과로 study 데이터베이스에 9개의 테이블이 생겼습니다.

> **github 링크**
> 
> 
> 

# ⚡ 트러블 슈팅

---

<aside>
💡 실습하면서 생긴 문제들에 대해서, **이슈 - 문제 - 해결** 순서로 작성해주세요.

</aside>

<aside>
💡 스스로 해결하기 어렵다면? 스터디원들에게 도움을 요청하거나 **너디너리의 지식IN 채널에 질문**해보세요!

</aside>

- ⚡이슈 작성 예시 (이슈가 생기면 아래를 복사해서 No.1, No.2, No3 … 으로 작성해서 트러블 슈팅을 꼭 해보세요!)
    
    **`이슈`**
    
    👉 앱 실행 중에 노래 다음 버튼을 누르니까 앱이 종료되었다.
    
    **`문제`**
    
    👉 노래클래스의 데이터리스트의 Size를 넘어서 NullPointException이 발생하여 앱이 종료된 것이었다. 
    
    **`해결`**
    
    👉  노래 다음 버튼을 눌렀을 때 데이터리스트의 Size를 검사해 Size보다 넘어가려고 하면 다음으로 넘어가는 메서드를 실행시키지 않고, 첫 노래로 돌아가게끔 해결
    
    **`참고레퍼런스`**
    
    - 링크
- ⚡이슈 No.1
    
    문제: 데이터베이스와 JPA 가 연동되지 않았다.
    
    해결: yml 파일을 수정하여서 해결하였다.
    
    2.
    
    문제: 테이블이 생성되지 않음
    
    원인: studyapplication 파일과 domain 파일이 서로 같은 파일 내에 있지 않아 엔티티테이블을 불러오지 못했다.
    
    해결: 같은 파일 내에 studyApplication 파일과 domain의 엔티티파일을 만들자 해결되었다.
    

       3. 

문제: alarm 테이블만 생성되지 않음

원인: enums으로 표현한 상태에 check라는 상태변수를 사용했는데 이는 sql내에서 사용하는 변수로 오류가 발생해 이 테이블만 불러오지 못했다.

해결: 상태변수명을 comfirm으로 바꾸어 해결했다.

---

Copyright © 2023 최용욱(똘이) All rights reserved.

Copyright © 2024 신수정(베뉴) All rights reserved.