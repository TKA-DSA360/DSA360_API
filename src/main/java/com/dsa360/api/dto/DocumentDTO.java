	package com.dsa360.api.dto;
	
	import javax.validation.constraints.NotBlank;
	import javax.validation.constraints.NotNull;
	import javax.validation.constraints.Pattern;
	import javax.validation.constraints.Size;
	
	import org.springframework.web.multipart.MultipartFile;
	
	import com.dsa360.api.constants.DocumentStatus;
	import com.dsa360.api.constants.DocumentType;
	
	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class DocumentDTO {
	
		private String id;
		
		@NotNull(message = "Document type is required")
	    private DocumentType documentType;
	
	    @NotBlank(message = "Document name is required")
	    @Size(min = 2, max = 100, message = "Document name should be between 2 and 100 characters")
	    private String documentName;
	
	    @NotBlank(message = "Status is required")
	    @Pattern(regexp = "Uploaded|Verified|Rejected|Pending", message = "Invalid status value")
	    private String status = DocumentStatus.UPLOADED.getValue();
	
	    @NotBlank(message = "Customer ID is required")
	    private String customerId;
	
	    @NotNull(message = "File is required")
	    private MultipartFile file;
	    
	    @NotBlank(message = "Comment is required")
	    private String comment;
	}
