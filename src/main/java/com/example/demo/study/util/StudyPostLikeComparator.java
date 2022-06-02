package com.example.demo.study.util;

import java.util.Comparator;
import java.util.Objects;

public class StudyPostLikeComparator  implements Comparator<StudyPostLikeCalc> {
    int ret=0;

    @Override
    public int compare(StudyPostLikeCalc s1, StudyPostLikeCalc s2) {
        if(s1.getLikeCount() < s2.getLikeCount()) {
            ret = 1;
        }
        else if(s1.getLikeCount() > s2.getLikeCount()) {
            ret = -1;
        }else{
            if(Objects.equals(s1.getLikeCount(), s2.getLikeCount())) {
                if(s1.getStudyPost().getStudyPostId()<s2.getStudyPost().getStudyPostId()){
                    ret=1;
                }else{
                    ret=-1;
                }
            }
        }
        return ret;
    }
}
