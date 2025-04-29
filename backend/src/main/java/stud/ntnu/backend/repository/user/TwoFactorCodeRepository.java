package stud.ntnu.backend.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.TwoFactorCode;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Integer> {
    Optional<TwoFactorCode> findByEmail(String email);
    void deleteByEmail(String email);
}
