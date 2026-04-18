package com.fdxsoft.entities;

import java.time.LocalDateTime;
import com.fdxsoft.enums.RepeatLimitType;
import com.fdxsoft.enums.SendFrequency;
import com.fdxsoft.enums.SendingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "wysiwyg")
public class WYSIWYGEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private SendFrequency sendFrequency;

    private LocalDateTime dateTimeSending;

    private LocalDateTime repeatEachTimeAt;

    @Enumerated(EnumType.STRING)
    private RepeatLimitType repeatLimitType;

    private Integer repeatQuantity;

    private LocalDateTime repeatEndDate;

    private String emailList;

    @Column(name = "creation_date", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime creationDate;

    @OneToOne(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private SchedulerEntity scheduler;

    public void calculateSchedulerForSave() {
        SchedulerEntity calcScheduler = new SchedulerEntity();

        switch (this.sendFrequency) {
            case IMMEDIATE:
                this.scheduler = null; // En este caso no debe guardar info en la BD
                break;
            case SCHEDULED:
                calcScheduler.setScheduledAt(this.dateTimeSending);
                calcScheduler.setNextScheduledAt(this.dateTimeSending); // Como solo se envia una unica vez, termina el
                                                                        // mismo dia, no hay siguiente envio
                calcScheduler.setAttempts(0); // Inicialmente los intentos deben de ser 0
                calcScheduler.setMaxAttempts(3); // TODO:Este valor se debe de cambiar despues cuando se tenga el modulo
                                                 // de configuracion general del sistema
                calcScheduler.setInitDate(this.dateTimeSending); // Inicia el mismo dia de su envio
                calcScheduler.setEndDate(this.dateTimeSending); // Debe terminar el mismo dia de su envio
                calcScheduler.setStatus(SendingStatus.PROGRAMMED); // Programado es el primer estatus que tiene que
                                                                   // llevar cuando se guarda en BD
                calcScheduler.setScheduledTimes(1); // Se programa para 1 unico envio, ya que es una unica fecha
                calcScheduler.setSentTimes(0L); // Si se logra enviar exitosamente se incrementa en 1
                // Finalmente se agrega el objeto a la entidad WYSIWYG
                this.scheduler = calcScheduler;
                break;
            case DAILY:
                switch (this.getRepeatLimitType()) {
                    case UNLIMITED:
                        // En caso de que sea ilimitado, solo debe de tener una fecha de inicio
                        calcScheduler.setScheduledAt(this.getRepeatEachTimeAt());
                        calcScheduler.setNextScheduledAt(this.getRepeatEachTimeAt().plusDays(1)); // Se envia sin limite
                                                                                                  // todos los dias uno
                                                                                                  // tras otro
                        calcScheduler.setAttempts(0); // Inicialmente los intentos deben de ser 0
                        calcScheduler.setMaxAttempts(3); // TODO:Este valor se debe de cambiar despues cuando se tenga
                                                         // el modulo
                                                         // de configuracion general del sistema
                        calcScheduler.setInitDate(this.getRepeatEachTimeAt()); // Inicia el mismo dia de su envio
                        calcScheduler.setEndDate(null); // En este caso nunca debe terminar, por eso va nulo siempre
                        calcScheduler.setStatus(SendingStatus.PROGRAMMED); // Programado es el primer estatus que debe
                                                                           // tener
                        calcScheduler.setScheduledTimes(0); // En este caso no se sabe cuantas veces se va a enviar, por
                                                            // lo que se pone 0
                        calcScheduler.setSentTimes(0L); // Si se logra enviar exitosamente se incrementa en 1 cada dia
                        break;
                    case QUANTITY:
                        calcScheduler.setScheduledAt(this.getRepeatEachTimeAt());
                        calcScheduler.setNextScheduledAt(this.getRepeatEachTimeAt().plusDays(1));
                        calcScheduler.setAttempts(0); // Inicialmente los intentos deben de ser 0
                        calcScheduler.setMaxAttempts(3); // TODO:Este valor se debe de cambiar despues cuando se tenga
                                                         // el modulo
                                                         // de configuracion general del sistema
                        calcScheduler.setInitDate(this.getRepeatEachTimeAt()); // Inicia el mismo dia de su envio
                        calcScheduler.setEndDate(this.getRepeatEachTimeAt().plusDays(this.getRepeatQuantity()));
                        calcScheduler.setStatus(SendingStatus.PROGRAMMED); // Programado es el primer estatus que debe
                                                                           // tener
                        calcScheduler.setScheduledTimes(this.getRepeatQuantity()); // Aqui si sabemos la cantidad
                        calcScheduler.setSentTimes(0L); // Si se logra enviar exitosamente se incrementa en 1 cada dia
                        break;
                    case END_DATE:
                        calcScheduler.setScheduledAt(this.getRepeatEachTimeAt());
                        calcScheduler.setNextScheduledAt(this.getRepeatEachTimeAt().plusDays(1)); //
                        calcScheduler.setAttempts(0); // Inicialmente los intentos deben de ser 0
                        calcScheduler.setMaxAttempts(3); // TODO:Este valor se debe de cambiar despues cuando se tenga
                                                         // el modulo
                                                         // de configuracion general del sistema
                        calcScheduler.setInitDate(this.getRepeatEachTimeAt()); // Inicia el mismo dia de su envio
                        calcScheduler.setEndDate(this.getRepeatEndDate());
                        calcScheduler.setStatus(SendingStatus.PROGRAMMED); // Programado es el primer estatus
                        calcScheduler.setScheduledTimes(
                                this.getRepeatEndDate().getDayOfMonth() - this.getRepeatEachTimeAt().getDayOfMonth());
                        calcScheduler.setSentTimes(0L); // Si se logra enviar exitosamente se incrementa en 1 cada dia
                        break;
                }
                this.scheduler = calcScheduler;
                break;
            default:
                break;
        }
    }

}
