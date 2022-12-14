package ru.set404.AdsMetrika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.set404.AdsMetrika.models.Settings;
import ru.set404.AdsMetrika.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    Optional<Settings> findSettingsByOwner(User user);
    @Query("select s from Settings s where s.spreadSheetScheduleEnabled = true or s.telegramEnabled = true")
    List<Settings> findAllEnabledTasks();
}
