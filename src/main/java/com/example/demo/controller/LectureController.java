package com.example.demo.controller;
import com.example.demo.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


//@RestController

@Controller
//@RequestMapping("/lectures")
public class LectureController {
    private LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/lecture")
    public String hi(){
        return "hi";
    }

//    @PostMapping("/lectures")
//    public void createLecture(LectureDto lectureDto){
//        System.out.println("\"hie\" = " + "hie");
//        System.out.println("lectureDto = " + lectureDto);
//        String lectureUrl = lectureDto.getLectureUrl();
//        String lectureTitle = lectureDto.getLectureTitle();
//        String lecturer = lectureDto.getLecturer();
//        String siteName = lectureDto.getSiteName();
//        String thumbnailUrl = lectureDto.getThumbnailUrl();
////        List<String> hashtags = lectureDto.getHashtags();
//        // 여기까지는 lecture table에 들어가는 것
//        System.out.println("lectureUrl = " + lectureUrl);
//        int rate = lectureDto.getRate();
//        String commentTitle = lectureDto.getCommentTitle();
//        String comment = lectureDto.getComment();
//
//        Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
//        lectureService.saveLecture(lecture);
//        return "success lecture";
//        return new ResponseEntity<>(new ResponseMessage(201,"강의 리뷰가 등록되었습니다."), HttpStatus.OK);
    }

