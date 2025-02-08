package iscae.mr.jwt_spring_boot.dao.repositories;
import iscae.mr.jwt_spring_boot.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);


}
