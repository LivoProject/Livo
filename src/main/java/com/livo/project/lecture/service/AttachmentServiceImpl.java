package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Attachment;
import com.livo.project.lecture.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public List<Attachment> getAttachmentsByLectureId(int lectureId) {
        return attachmentRepository.findByLectureId(lectureId);
    }
}
