package com.fdxsoft.services.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.repositories.WYSIWYGRepository;
import com.fdxsoft.services.WYSIWYGService;

@Service
public class WYSIWYGServiceImpl implements WYSIWYGService {

	@Autowired
	private WYSIWYGRepository wysiwygRepository;

	@Override
	public GenericResponseDTO<WYSIWYGEntity> findByEntityName(String templateName) {
		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
		Optional<WYSIWYGEntity> wysiwygEntity = wysiwygRepository.findByTemplateName(templateName);
		if (wysiwygEntity.isPresent()) {
			response.setMessage("Plantilla Encontrada");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(wysiwygEntity.get()));
		} else {
			response.setMessage("Plantilla no encontrada");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
		}
		return response;
	}

	@Override
	public GenericResponseDTO<WYSIWYGEntity> save(WYSIWYGRequestDTO wysiwygRequestDTO) {

		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();

		WYSIWYGEntity wysiwygEntity = mapToEntity(wysiwygRequestDTO);

		// Validar duplicados solo cuando sea alta nueva
		if (wysiwygEntity.getId() == null
				&& wysiwygRepository.findByTemplateName(wysiwygEntity.getTemplateName()).isPresent()) {

			response.setMessage("Ya existe una plantilla con este nombre");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.CONFLICT.value());
			return response;
		}

		WYSIWYGEntity savedEntity = wysiwygRepository.save(wysiwygEntity);

		try {

			// Carpeta principal de la plantilla
			Path templateFolder = Paths.get("src/main/resources/static/f_" + savedEntity.getId());

			// Subcarpeta para imágenes
			Path imageFolder = templateFolder.resolve("img");

			Files.createDirectories(templateFolder);
			Files.createDirectories(imageFolder);

			// Guardar el HTML
			if (wysiwygRequestDTO.getHtmlInput() != null && !wysiwygRequestDTO.getHtmlInput().isBlank()) {

				Path htmlFile = templateFolder.resolve("template.html");

				Files.writeString(htmlFile, wysiwygRequestDTO.getHtmlInput(), StandardCharsets.UTF_8,
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}

			// Guardar imágenes
			if (wysiwygRequestDTO.getImages() != null && wysiwygRequestDTO.getImages().length > 0) {

				for (MultipartFile image : wysiwygRequestDTO.getImages()) {

					if (image != null && !image.isEmpty()) {

						String originalFileName = image.getOriginalFilename();

						if (originalFileName == null || originalFileName.isBlank()) {
							continue;
						}

						Path imagePath = imageFolder.resolve(originalFileName);

						Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			response.setMessage(
					"La plantilla se guardó en BD, pero ocurrió un error al guardar el HTML o las imágenes");
			response.setStatus("warning");
			response.setHttpStatus(HttpStatus.PARTIAL_CONTENT.value());
			response.setData(List.of(savedEntity));
			return response;
		}

		response.setMessage("Plantilla guardada exitosamente");
		response.setStatus("success");
		response.setHttpStatus(HttpStatus.OK.value());
		response.setData(List.of(savedEntity));

		return response;
	}

	@Override
	public GenericResponseDTO<WYSIWYGEntity> delete(Long id) {
		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
		Optional<WYSIWYGEntity> wysiwygEntity = wysiwygRepository.findById(id);
		if (wysiwygEntity.isPresent()) {
			try {
				wysiwygRepository.deleteById(id);
				response.setMessage("Plantilla Eliminada con Exito");
				response.setStatus("success");
				response.setHttpStatus(HttpStatus.OK.value());
				response.setData(List.of(wysiwygEntity.get()));
			} catch (Exception e) {
				response.setMessage(
						"La plantilla no pudo ser eliminada debido a problemas del servicio. Intente mas tarde.");
				response.setStatus("error");
				response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} else {
			response.setMessage("No fue posible eliminar la plantilla por que no existe.");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
		}
		return response;
	}

	@Override
	public GenericResponseDTO<WYSIWYGEntity> update(WYSIWYGRequestDTO wysiwygRequestDTO) {
		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
		WYSIWYGEntity wysiwygEntity = mapToEntity(wysiwygRequestDTO);
		if (wysiwygRepository.findById(wysiwygEntity.getId()).isPresent()) {
			WYSIWYGEntity updatedEntity = wysiwygRepository.save(wysiwygEntity);
			response.setMessage("Platilla actualizada con exito.");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(updatedEntity));
			return response;
		} else {
			response.setMessage("La plantilla no se pudo actualizar por que no existe.");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
		}
		return response;
	}

	@Override
	public GenericResponseDTO<WYSIWYGEntity> getAll() {
		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
		List<WYSIWYGEntity> wysiwygEntityList = wysiwygRepository.findAll();
		if (!wysiwygEntityList.isEmpty()) {
			response.setMessage("Plantilla Encontrada");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(wysiwygEntityList);
		} else {
			response.setMessage("Plantilla no encontrada");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
		}
		return response;
	}

	@Override
	public WYSIWYGEntity mapToEntity(WYSIWYGRequestDTO wysiwygRequestDTO) {
		Optional<WYSIWYGEntity> tmpEntity = null;
		WYSIWYGEntity wysiwygEntity = null;

		try {
			tmpEntity = wysiwygRepository.findById(wysiwygRequestDTO.getId());
			if (tmpEntity.isPresent())
				wysiwygEntity = tmpEntity.get();
			else
				wysiwygEntity = new WYSIWYGEntity();
		} catch (Exception e) {
			wysiwygEntity = new WYSIWYGEntity();
		}

		if (wysiwygRequestDTO.getId() != null) {
			wysiwygEntity.setId(wysiwygRequestDTO.getId());
		}

		if (wysiwygRequestDTO.getTemplateName() != null) {
			wysiwygEntity.setTemplateName(wysiwygRequestDTO.getTemplateName());
		}

		if (wysiwygRequestDTO.getDescription() != null) {
			wysiwygEntity.setDescription(wysiwygRequestDTO.getDescription());
		}

		if (wysiwygRequestDTO.getSendFrequency() != null) {
			wysiwygEntity.setSendFrequency(wysiwygRequestDTO.getSendFrequency());
		}

		if (wysiwygRequestDTO.getDateTimeSending() != null) {
			wysiwygEntity.setDateTimeSending(wysiwygRequestDTO.getDateTimeSending());
		}

		if (wysiwygRequestDTO.getRepeatEachTimeAt() != null) {
			wysiwygEntity.setRepeatEachTimeAt(wysiwygRequestDTO.getRepeatEachTimeAt());
		}

		if (wysiwygRequestDTO.getRepeatLimitType() != null) {
			wysiwygEntity.setRepeatLimitType(wysiwygRequestDTO.getRepeatLimitType());
		}

		if (wysiwygRequestDTO.getRepeatQuantity() != null) {
			wysiwygEntity.setRepeatQuantity(wysiwygRequestDTO.getRepeatQuantity());
		}

		if (wysiwygRequestDTO.getRepeatEndDate() != null) {
			wysiwygEntity.setRepeatEndDate(wysiwygRequestDTO.getRepeatEndDate());
		}

		if (wysiwygRequestDTO.getEmailList() != null) {
			wysiwygEntity.setEmailList(wysiwygRequestDTO.getEmailList());
		}

		return wysiwygEntity;
	}
}
