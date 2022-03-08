package com.example.demo.roadmap;

import com.example.demo.like.Like;
import com.example.demo.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "roadmapGroups")
@NoArgsConstructor
public class RoadMapGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapGroupId;

    @Column(nullable = false)
    private String roadmapGroupTitle;

    @Column
    private String roadmapGroupRecommendation; //추천대상

    @Column
    private Integer roadmapGroupStatus=1;  //0이면 삭제된 것

    @Column
    @CreatedDate
    private LocalDateTime roadmapGroupCreatedDate=LocalDateTime.now();

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user; //직성자 객체 저장

    @OneToMany(mappedBy = "roadmapGroup",targetEntity = Like.class)
    @JsonManagedReference
    private List<Like> likes =new ArrayList<>();

    @OneToMany(mappedBy="roadmapGroup",targetEntity = RoadMap.class)
    @JsonManagedReference
    private List<RoadMap> roadMaps=new ArrayList<>();
}
