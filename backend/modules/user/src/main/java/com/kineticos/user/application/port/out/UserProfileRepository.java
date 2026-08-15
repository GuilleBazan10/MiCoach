package com.kineticos.user.application.port.out;

import com.kineticos.user.domain.*;
import java.util.List;
import java.util.Optional;

public interface UserProfileRepository {
    Optional<UserProfile> findByUserId(Long userId);
    UserProfile save(UserProfile profile);
    List<UserGoal> findGoals(Long profileId);
    UserGoal saveGoal(UserGoal goal);
    void deleteGoal(Long id);
    List<UserPathology> findPathologies(Long profileId);
    UserPathology savePathology(UserPathology pathology);
    void deletePathology(Long id);
    List<UserInjury> findInjuries(Long profileId);
    UserInjury saveInjury(UserInjury injury);
    void deleteInjury(Long id);
    List<UserMedication> findMedications(Long profileId);
    UserMedication saveMedication(UserMedication medication);
    void deleteMedication(Long id);
}
