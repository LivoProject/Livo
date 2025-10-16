package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Attachment;

import java.util.List;

public interface AttachmentService {
    List<Attachment> getAttachmentsByLectureId(int lectureId);
}
