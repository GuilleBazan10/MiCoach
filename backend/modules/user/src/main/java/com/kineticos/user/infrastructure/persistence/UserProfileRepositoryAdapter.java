package com.kineticos.user.infrastructure.persistence;

import com.kineticos.user.application.port.out.UserProfileRepository;
import com.kineticos.user.domain.UserGoal;
import com.kineticos.user.domain.UserInjury;
import com.kineticos.user.domain.UserMedication;
import com.kineticos.user.domain.UserPathology;
import com.kineticos.user.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA del puerto {@link UserProfileRepository}.
 */
@Component
public class UserProfileRepositoryAdapter implements UserProfileRepository {

    private final UserProfileJpaRepository profileRepository;
    private final UserGoalJpaRepository goalRepository;
    private final UserPathologyJpaRepository pathologyRepository;
    private final UserInjuryJpaRepository injuryRepository;
    private final UserMedicationJpaRepository medicationRepository;

    public UserProfileRepositoryAdapter(UserProfileJpaRepository profileRepository,
                                        UserGoalJpaRepository goalRepository,
                                        UserPathologyJpaRepository pathologyRepository,
                                        UserInjuryJpaRepository injuryRepository,
                                        UserMedicationJpaRepository medicationRepository) {
        this.profileRepository = profileRepository;
        this.goalRepository = goalRepository;
        this.pathologyRepository = pathologyRepository;
        this.injuryRepository = injuryRepository;
        this.medicationRepository = medicationRepository;
    }

    // ------------------------- Perfil -------------------------

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        return profileRepository.findByUserId(userId).map(ProfileMapper::toDomain);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        return ProfileMapper.toDomain(profileRepository.save(ProfileMapper.toJpa(profile)));
    }

    // ------------------------- Objetivos -------------------------

    @Override
    public List<UserGoal> findGoals(Long profileId) {
        return goalRepository.findByProfileIdOrderByPriorityAsc(profileId).stream()
                .map(GoalMapper::toDomain).toList();
    }

    @Override
    public UserGoal saveGoal(UserGoal goal) {
        return GoalMapper.toDomain(goalRepository.save(GoalMapper.toJpa(goal)));
    }

    @Override
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    // ------------------------- Patologías -------------------------

    @Override
    public List<UserPathology> findPathologies(Long profileId) {
        return pathologyRepository.findByProfileId(profileId).stream()
                .map(PathologyMapper::toDomain).toList();
    }

    @Override
    public UserPathology savePathology(UserPathology pathology) {
        return PathologyMapper.toDomain(pathologyRepository.save(PathologyMapper.toJpa(pathology)));
    }

    @Override
    public void deletePathology(Long id) {
        pathologyRepository.deleteById(id);
    }

    // ------------------------- Lesiones -------------------------

    @Override
    public List<UserInjury> findInjuries(Long profileId) {
        return injuryRepository.findByProfileId(profileId).stream()
                .map(InjuryMapper::toDomain).toList();
    }

    @Override
    public UserInjury saveInjury(UserInjury injury) {
        return InjuryMapper.toDomain(injuryRepository.save(InjuryMapper.toJpa(injury)));
    }

    @Override
    public void deleteInjury(Long id) {
        injuryRepository.deleteById(id);
    }

    // ------------------------- Medicación -------------------------

    @Override
    public List<UserMedication> findMedications(Long profileId) {
        return medicationRepository.findByProfileId(profileId).stream()
                .map(MedicationMapper::toDomain).toList();
    }

    @Override
    public UserMedication saveMedication(UserMedication medication) {
        return MedicationMapper.toDomain(
                medicationRepository.save(MedicationMapper.toJpa(medication)));
    }

    @Override
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }
}