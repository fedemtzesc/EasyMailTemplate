package com.fdxsoft;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fdxsoft.entities.PermissionEntity;
import com.fdxsoft.entities.RoleEntity;
import com.fdxsoft.entities.UserEntity;
import com.fdxsoft.enums.RoleEnum;
import com.fdxsoft.repositories.UserRepository;

@SpringBootApplication
public class EasyMailTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyMailTemplateApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder crypter) {
		return args -> {
			// PASO #1 Creamos los permisos, vamos de atras hacia adelante
			PermissionEntity configPermission = PermissionEntity.builder().name("CONFIG").build();
			PermissionEntity readPermission = PermissionEntity.builder().name("READ").build();
			PermissionEntity writePermission = PermissionEntity.builder().name("WRITE").build();
			PermissionEntity updatePermission = PermissionEntity.builder().name("UPDATE").build();
			PermissionEntity deletePermission = PermissionEntity.builder().name("DELETE").build();
			PermissionEntity printPermission = PermissionEntity.builder().name("PRINT").build();
			PermissionEntity emailPermission = PermissionEntity.builder().name("EMAIL").build();
			// PASO #2 Creamos los ROles, que vendrian a ser como los grupos
			RoleEntity adminRole = RoleEntity
					.builder().role(RoleEnum.ADMIN).permissions(Set.of(configPermission, readPermission,
							writePermission, updatePermission, deletePermission, printPermission, emailPermission))
					.build();
			RoleEntity userRole = RoleEntity
					.builder().role(RoleEnum.USER).permissions(Set.of(configPermission, readPermission,
							writePermission, updatePermission, deletePermission, emailPermission))
					.build();
			RoleEntity managerRole = RoleEntity
					.builder().role(RoleEnum.MANAGER).permissions(Set.of(configPermission,readPermission, printPermission, emailPermission))
					.build();
			RoleEntity guestRole = RoleEntity
					.builder().role(RoleEnum.GUEST).permissions(Set.of(readPermission))
					.build();
			RoleEntity developerRole = RoleEntity
					.builder().role(RoleEnum.DEVELOPER).permissions(Set.of(configPermission, readPermission,printPermission, emailPermission))
					.build();
			RoleEntity operatorRole = RoleEntity
					.builder().role(RoleEnum.OPERATOR).permissions(Set.of(readPermission,emailPermission))
					.build();
			// PASO 3 Creamos los usuarios
			UserEntity userFederico = UserEntity.builder()
					.username("federico")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(adminRole))
					.name("Federico")
					.lastName("Martinez Escamilla")
					.email("fedemtzesc@hotmail.com")
					.phone("+528110099414")
					.build();
			UserEntity userYolanda = UserEntity.builder()
					.username("yolanda")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(managerRole))
					.name("Maria Yolanda")
					.lastName("Belmares Carrizales")
					.email("yolanda.belmares.carrizales@gmail.com")
					.phone("+528117173432")
					.build();
			UserEntity userValeria = UserEntity.builder()
					.username("valeria")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(userRole))
					.name("Valeria Estefania")
					.lastName("Martinez Belmares")
					.email("valitamtz@gmail.com")
					.phone("+528117173948")
					.build();
			UserEntity userSebastian = UserEntity.builder()
					.username("sebastian")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(guestRole))
					.name("Sebastian Alejandro")
					.lastName("Ramos Belmares")
					.email("elsebas2012@gmail.com")
					.phone("+528117171243")
					.build();
			UserEntity userFDXSOFT = UserEntity.builder()
					.username("fdxsoft")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(developerRole))
					.name("FDSOFT")
					.lastName("S.A. de C.V.")
					.email("gerencia@fdxsoft.com")
					.phone("+528111013875")
					.build();
			UserEntity userXimena = UserEntity.builder()
					.username("ximena")
					.password(crypter.encode("Calibre3006"))
					.enabled(true)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.roles(Set.of(operatorRole))
					.name("Ximena Itzel")
					.lastName("Ramos Belmares")
					.email("ximeloca@gmail.com")
					.phone("+528111012359")
					.build();
			
			userRepository.saveAll(List.of(userFederico,userYolanda,userValeria,userSebastian,userFDXSOFT,userXimena));
			
		};
	}
}
