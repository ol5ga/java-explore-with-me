package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.compilations.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation,Long> {
    List<Compilation> findAllByPinned(boolean pinned, Pageable page);
}
