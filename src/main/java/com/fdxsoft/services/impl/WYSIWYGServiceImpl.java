package com.fdxsoft.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGListDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGViewDTO;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.repositories.WYSIWYGRepository;
import com.fdxsoft.services.WYSIWYGService;
import com.fdxsoft.utils.DateTimeUtils;

@Service
public class WYSIWYGServiceImpl implements WYSIWYGService {

	@Autowired
	private WYSIWYGRepository wysiwygRepository;

	@Override
	public GenericResponseDTO<WYSIWYGViewDTO> findById(Long id) {
		GenericResponseDTO<WYSIWYGViewDTO> response = new GenericResponseDTO<>();
		Optional<WYSIWYGEntity> wysiwygEntity = wysiwygRepository.findById(id);

		if (wysiwygEntity.isEmpty()) {
			response.setMessage("Plantilla no encontrada");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
			response.setData(Collections.emptyList());
			return response;
		}

		try {
			WYSIWYGEntity entity = wysiwygEntity.get();
			WYSIWYGViewDTO viewDTO = new WYSIWYGViewDTO();
			// =========================
			// MAPEO BD → DTO
			// =========================
			viewDTO.setId(entity.getId());
			viewDTO.setTemplateName(entity.getTemplateName());
			viewDTO.setDescription(entity.getDescription());
			viewDTO.setSendFrequency(entity.getSendFrequency());
			viewDTO.setDateTimeSending(entity.getDateTimeSending());
			viewDTO.setRepeatEachTimeAt(entity.getRepeatEachTimeAt());
			viewDTO.setRepeatLimitType(entity.getRepeatLimitType());
			viewDTO.setRepeatQuantity(entity.getRepeatQuantity());
			viewDTO.setRepeatEndDate(entity.getRepeatEndDate());
			viewDTO.setEmailList(entity.getEmailList());

			// =========================
			// HTML DESDE FILESYSTEM
			// =========================
			String basePath = "src/main/resources/static/emails";
			Path htmlPath = Paths.get(basePath, "f_" + entity.getId(), "template.html");

			if (Files.exists(htmlPath)) {
				String html = Files.readString(htmlPath, StandardCharsets.UTF_8);
				viewDTO.setHtmlInput(html);
			} else {
				viewDTO.setHtmlInput("");
			}

			// =========================
			// IMÁGENES DESDE FILESYSTEM
			// =========================
			Path imgDir = Paths.get(basePath, "f_" + entity.getId(), "img");

			Map<String, String> imagesMap = new HashMap<>();
			List<String> erroresImagenes = new ArrayList<>();

			if (Files.exists(imgDir) && Files.isDirectory(imgDir)) {
				try (Stream<Path> paths = Files.list(imgDir)) {

					paths
					  .filter(Files::isRegularFile)
					  .forEach(path -> {
					      try {
					          String fileName = path.getFileName().toString();
					          String lowerName = fileName.toLowerCase();

					          if (!(lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
					                lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif") ||
					                lowerName.endsWith(".webp") || lowerName.endsWith(".svg"))) {
					              return;
					          }

					          if (Files.size(path) > 2_000_000) {
					              erroresImagenes.add("Archivo demasiado grande: " + fileName);
					              return;
					          }

					          byte[] bytes = Files.readAllBytes(path);
					          String base64 = Base64.getEncoder().encodeToString(bytes);

					          String mimeType = Files.probeContentType(path);

					          if (mimeType == null) {
					              if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
					                  mimeType = "image/jpeg";
					              } else if (lowerName.endsWith(".png")) {
					                  mimeType = "image/png";
					              } else if (lowerName.endsWith(".gif")) {
					                  mimeType = "image/gif";
					              } else if (lowerName.endsWith(".webp")) {
					                  mimeType = "image/webp";
					              } else if (lowerName.endsWith(".svg")) {
					                  mimeType = "image/svg+xml";
					              } else {
					                  mimeType = "application/octet-stream";
					              }
					          }

					          String base64Url = "data:" + mimeType + ";base64," + base64;

					          imagesMap.put(fileName, base64Url);

					      } catch (IOException e) {
					          erroresImagenes.add("Error con archivo: " + path.getFileName() + ": " + e.getMessage());
					      }
					  });

				} catch (IOException e) {
					response.setMessage("Error al listar imágenes. " + e.getMessage());
					response.setStatus("error");
					response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setData(Collections.emptyList());
					return response;
				}
			}

			if (!erroresImagenes.isEmpty()) {
				response.setMessage("Plantilla cargada con advertencias: " + erroresImagenes);
			} else {
				response.setMessage("Plantilla Cargada Exitosamente");
			}

			// =========================
			// RESPONSE
			// =========================
			viewDTO.setImages(imagesMap);
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(viewDTO));

			return response;
		} catch (Exception e) {
			response.setMessage("Error al cargar la plantilla. " + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setData(Collections.emptyList());
			return response;
		}
	}

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
		if (wysiwygEntity.getId() == null
				&& wysiwygRepository.findByTemplateName(wysiwygEntity.getTemplateName()).isPresent()) {
			response.setMessage("Ya existe una plantilla con este nombre");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.CONFLICT.value());
			return response;
		}

		WYSIWYGEntity savedEntity = wysiwygRepository.save(wysiwygEntity);
		try {
			Path templateFolder = Paths.get("src/main/resources/static/emails/f_" + savedEntity.getId());
			Path imageFolder = templateFolder.resolve("img");
			Files.createDirectories(templateFolder);
			if (Files.exists(imageFolder)) {
				FileSystemUtils.deleteRecursively(imageFolder);
			}
			Files.createDirectories(imageFolder);
			if (wysiwygRequestDTO.getHtmlInput() != null && !wysiwygRequestDTO.getHtmlInput().isBlank()) {
				Path htmlFile = templateFolder.resolve("template.html");
				Files.writeString(htmlFile, wysiwygRequestDTO.getHtmlInput(), StandardCharsets.UTF_8,
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}
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
	public GenericResponseDTO<WYSIWYGEntity> update(WYSIWYGRequestDTO wysiwygRequestDTO) {
		GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
		WYSIWYGEntity wysiwygEntity = mapToEntity(wysiwygRequestDTO);
		if (wysiwygEntity.getTemplateName() != null && !wysiwygEntity.getTemplateName().isBlank()) {
			Optional<WYSIWYGEntity> wysiwygOptional = wysiwygRepository
					.findByTemplateName(wysiwygEntity.getTemplateName());
			if (wysiwygOptional.isPresent()) {
				WYSIWYGEntity wysiwygFound = wysiwygOptional.get();
				if (!wysiwygFound.getId().equals(wysiwygEntity.getId())) {
					response.setMessage(
							"El nombre nuevo de plantilla que quiere modificar ya le pertenece a otra plantilla.");
					response.setStatus("error");
					response.setHttpStatus(HttpStatus.CONFLICT.value());
					return response;
				}
			}
		}
		if (wysiwygRepository.findById(wysiwygEntity.getId()).isPresent()) {
			WYSIWYGEntity updatedEntity = wysiwygRepository.save(wysiwygEntity);
			try {
				Path templateFolder = Paths.get("src/main/resources/static/emails/f_" + updatedEntity.getId());
				Path imageFolder = templateFolder.resolve("img");
				Files.createDirectories(templateFolder);
				if (wysiwygRequestDTO.getHtmlInput() != null && !wysiwygRequestDTO.getHtmlInput().isBlank()) {
					Path htmlFile = templateFolder.resolve("template.html");
					Files.writeString(htmlFile, wysiwygRequestDTO.getHtmlInput(), StandardCharsets.UTF_8,
							StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				}

				if (wysiwygRequestDTO.getImages() != null && wysiwygRequestDTO.getImages().length > 0) {
					if (Files.exists(imageFolder)) {
						FileSystemUtils.deleteRecursively(imageFolder);
					}
					Files.createDirectories(imageFolder);
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
				response.setMessage(
						"La plantilla se guardó en BD, pero ocurrió un error al guardar el HTML o las imágenes");
				response.setStatus("warning");
				response.setHttpStatus(HttpStatus.PARTIAL_CONTENT.value());
				response.setData(List.of(updatedEntity));
				return response;
			}
			response.setMessage("Plantilla e imagenes actualizadas con exito.");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(updatedEntity));
		} else {
			response.setMessage("La plantilla no se pudo actualizar por que no existe.");
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.NOT_FOUND.value());
		}
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
	public GenericResponseDTO<WYSIWYGListDTO> getAll(int page, int size, String search) {

		GenericResponseDTO<WYSIWYGListDTO> response = new GenericResponseDTO<>();
		response.setPaging(true);

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<WYSIWYGEntity> entityPage;

		if (search != null && !search.trim().isEmpty()) {
			entityPage = wysiwygRepository.findByTemplateNameContainingIgnoreCase(search, pageable);
		} else {
			entityPage = wysiwygRepository.findAll(pageable);
		}

		List<WYSIWYGListDTO> dtoList = entityPage.getContent().stream().map(entity -> {

			WYSIWYGListDTO dto = new WYSIWYGListDTO();

			dto.setId(entity.getId());
			dto.setTemplateName(entity.getTemplateName());

			switch (entity.getSendFrequency()) {
			case "I":
				dto.setSendFrequencyDescription("Envío Inmediato");
				dto.setRepeatLimitDescription("Envio inmediato al ser llamado via API REST.");
				break;

			case "S":
				dto.setSendFrequencyDescription("Envio en cierta fecha y a cierta hora");
				dto.setRepeatLimitDescription("El envio sera realizado el dia "
						+ DateTimeUtils.DescriptiveFormat(entity.getDateTimeSending()));
				break;

			case "D":
				dto.setSendFrequencyDescription("Repetir cierta cantidad de días a cierta hora.");

				switch (entity.getRepeatLimitType()) {
				case "UNLIMITED":
					dto.setRepeatLimitDescription("Sin límite de tiempo.");
					break;

				case "QUANTITY":
					dto.setRepeatLimitDescription(
							entity.getRepeatQuantity() + " días a las " + entity.getRepeatEachTimeAt());
					break;

				case "END_DATE":
					dto.setRepeatLimitDescription("Todos los días hasta el día " + entity.getRepeatEndDate());
					break;

				default:
					dto.setRepeatLimitDescription(entity.getRepeatLimitType());
				}
				break;

			default:
				dto.setSendFrequencyDescription(entity.getSendFrequency());
			}

			return dto;

		}).toList();

		response.setData(dtoList);
		response.setCurrentPage(entityPage.getNumber() + 1);
		response.setLastPage(entityPage.getTotalPages() > 0 ? entityPage.getTotalPages() : 1);
		response.setTotalRecords(entityPage.getTotalElements());

		if (dtoList.isEmpty()) {
			response.setMessage("No se encontraron plantillas registradas");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
		} else {
			response.setMessage("Plantillas encontradas");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
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
