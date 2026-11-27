package com.skyline.skatecross.modules.event.service;

import com.skyline.skatecross.modules.assignment.entity.EventAssignment;
import com.skyline.skatecross.modules.assignment.repository.EventAssignmentRepository;
import com.skyline.skatecross.modules.category.dto.CategoryCreateRequestDto;
import com.skyline.skatecross.modules.category.dto.EventCategoryCreateRequestDto;
import com.skyline.skatecross.modules.category.entity.Category;
import com.skyline.skatecross.modules.category.mapper.CategoryMapper;
import com.skyline.skatecross.modules.event.dto.EventCreateRequestDto;
import com.skyline.skatecross.modules.event.dto.EventResponseDto;
import com.skyline.skatecross.modules.event.entity.Event;
import com.skyline.skatecross.modules.event.mapper.EventMapper;
import com.skyline.skatecross.modules.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventAssignmentRepository eventAssignmentRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto request) {
        // 1. Validasi logika tanggal pendaftaran
        if (request.getRegistrationCloseAt().isBefore(request.getRegistrationOpenAt())) {
            throw new IllegalArgumentException("Tanggal akhir pendaftaran tidak boleh sebelum tanggal mulai");
        }

        // 2. Map Event & generate event_code unik
        Event event = eventMapper.toEntity(request);
        String datePart = request.getEventDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String eventCode = "EVT-" + datePart + "-" + randomPart;
        event.setEventCode(eventCode);

        // 3. Map setiap kategori dan pasangkan ke Event
        List<EventCategoryCreateRequestDto> catDtos = request.getCategories();
        for (int i = 0; i < catDtos.size(); i++) {
            EventCategoryCreateRequestDto catDto = catDtos.get(i);

            if (catDto.getMaxAge() < catDto.getMinAge()) {
                throw new IllegalArgumentException("Usia maksimal tidak boleh lebih kecil dari usia minimal pada kategori: " + catDto.getName());
            }

            Category category = categoryMapper.toEntity(catDto);
            category.setCategoryCode(eventCode + "-C" + String.format("%02d", i + 1));

            // Jika jadwal daftar kategori tidak diisi khusus, ikuti jadwal event
            if (category.getRegistrationOpenAt() == null) {
                category.setRegistrationOpenAt(event.getRegistrationOpenAt());
            }
            if (category.getRegistrationCloseAt() == null) {
                category.setRegistrationCloseAt(event.getRegistrationCloseAt());
            }

            event.addCategory(category);
        }

        // 4. Simpan Event beserta seluruh Category (CascadeType.ALL)
        Event savedEvent = eventRepository.save(event);

        // 5. Simpan penugasan PIC Event dan PJ Kategori ke event_assignments
        List<EventAssignment> assignments = new ArrayList<>();

        EventAssignment picAssignment = new EventAssignment();
        picAssignment.setEventId(savedEvent.getId());
        picAssignment.setUserId(request.getPicUserId());
        picAssignment.setRoleCode("EVENT_PIC");
        assignments.add(picAssignment);

        List<Category> savedCategories = savedEvent.getCategories();
        for (int i = 0; i < savedCategories.size(); i++) {
            EventAssignment pjAssignment = new EventAssignment();
            pjAssignment.setEventId(savedEvent.getId());
            pjAssignment.setCategoryId(savedCategories.get(i).getId());
            pjAssignment.setUserId(catDtos.get(i).getPjUserId());
            pjAssignment.setRoleCode("CATEGORY_PJ");
            assignments.add(pjAssignment);
        }

        eventAssignmentRepository.saveAll(assignments);

        return eventMapper.toResponseDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan dengan ID: " + id));
        return eventMapper.toResponseDto(event);
    }
}