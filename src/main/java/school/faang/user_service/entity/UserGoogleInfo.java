package school.faang.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_google_info")
@Data
public class UserGoogleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sub")
    private String sub;

    @Column(name = "at_hash")
    private String atHash;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "iss")
    private String iss;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "picture")
    private String picture;

    @Column(name = "azp")
    private String azp;

    @Column(name = "name")
    private String name;

    @Column(name = "exp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime exp;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "iat")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime iat;

    @Column(name = "email")
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}