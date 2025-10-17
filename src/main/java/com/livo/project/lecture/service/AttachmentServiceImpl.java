package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Attachment;
import com.livo.project.lecture.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    public List<Attachment> getAttachmentsByLectureId(int lectureId) {
        return attachmentRepository.findByLectureId(lectureId);
    }
}
