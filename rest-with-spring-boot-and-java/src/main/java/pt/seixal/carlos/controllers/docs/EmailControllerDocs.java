package pt.seixal.carlos.controllers.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pt.seixal.carlos.data.dto.v1.request.EmailRequestDTO;

public interface EmailControllerDocs {

    @Operation(summary = "Send e-mail",
            description = "Sends an e-mail by providing details, subject and body!",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
	ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO);
	

    @Operation(summary = "Send e-mail with attachments",
            description = "Send an e-mail with attachment/s by providing details, subject and body!",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
	ResponseEntity<String> sendEmailWithAttachments(@RequestParam("emailRequest") String emailRequest, @RequestParam("attachment") List<MultipartFile> attachments);
}
