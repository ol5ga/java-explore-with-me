package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.compilations.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation,Long> {
}
