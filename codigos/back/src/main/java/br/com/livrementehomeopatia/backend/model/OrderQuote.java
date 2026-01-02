package br.com.livrementehomeopatia.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderQuote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String email;

    @OneToOne
    private Order order;

    @Embedded
    private Address address;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "medical_prescription")
    private byte[] medicalPrescription;

    @Column(length = 1000)
    private String observation;

}