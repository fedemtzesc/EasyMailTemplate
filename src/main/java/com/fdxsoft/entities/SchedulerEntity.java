package com.fdxsoft.entities;

import java.time.LocalDateTime;

import com.fdxsoft.enums.SendingStatus;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduler")
public class SchedulerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime scheduledAt;
	private LocalDateTime nextScheduledAt;
	private Integer attempts;
	private Integer maxAttempts;
	private LocalDateTime initDate;
	private LocalDateTime endDate;
	@Enumerated(EnumType.STRING)
	private SendingStatus status;
	private Integer scheduledTimes;
	private Long sentTimes;

	@OneToOne
	@JoinColumn(name = "fk_template_id", referencedColumnName = "id")
	private WYSIWYGEntity template;

}
