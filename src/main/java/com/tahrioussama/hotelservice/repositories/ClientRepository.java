package com.tahrioussama.hotelservice.repositories;

import com.tahrioussama.hotelservice.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {
    Client findByEmail(String email);

    boolean existsByEmail(String email);
}
