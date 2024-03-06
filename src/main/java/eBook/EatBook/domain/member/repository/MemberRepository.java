package eBook.EatBook.domain.member.repository;

import eBook.EatBook.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findMemberByEmail(String email);

    List<Member> findByIsSeller(boolean b);
}
