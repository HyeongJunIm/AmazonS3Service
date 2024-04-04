# AmazonS3Service

## 📌Amzon 
- AmazonS3, RDS, MySQL 연동하여 이미지 배포 
- AWS EC2, Route53(도메인 연결), AWS ELB, RDS를 설정하여 작성

## 코드 
<details><summary>S3설정</summary>
   - S3Config.java
  
  ```java  
    
        @Configuration
        public class S3Config {
        @Value("${cloud.aws.credentials.accesskey}")
        private String accessKey;
        @Value("${cloud.aws.credentials.secretkey}")
        private String secretKey;
        @Value("${cloud.aws.region.static}")
        private String region;
        
        @Bean
        public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials credentials =new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client)AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
          }
      }
      
  ``` 
</details>

<details><summary>Entity 설정</summary>
  - Image.java
  
  ```java
   @Entity
   @EntityListeners(AuditingEntityListener.class)
   @NoArgsConstructor
   public class Image {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     private String fileURL;
     @CreatedDate
     private LocalDateTime createdDate;

     @Builder
     public Image(Long id, String fileURL, LocalDateTime createdDate) {
      this.id = id;
      this.fileURL = fileURL;
      this.createdDate = createdDate;
      }
    }
  ```
</details>

<details><summary>DTO 설정</summary>
  - ImageDTO.java
  
  ```java
    @Data
    @NoArgsConstructor
    public class ImageDto {
    Long id;
    String fileURL;

    public ImageDto(String fileURL) {
        this.fileURL = fileURL;
    }

    public Image toEntity() {
        return Image.builder()
                .fileURL(fileURL)
                .build();
    }
  }
```
</details>

<details><summary>Controller</summary>

- ImageRestController.java
  ```java
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/files")
    public class ImageRestController {
     private final S3UploaderService s3UploaderService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(
            @RequestParam("multipartFiles")List<MultipartFile> multipartFiles)
    {
        return ResponseEntity.ok(s3UploaderService.insertFile(multipartFiles));
    }

  }

  ```
</details>

<details><summary>Service</summary>
  -S3UploaderService.java

  ```java
    @Slf4j
    @RequiredArgsConstructor
    @Service
    @Transactional(readOnly = true)
    public class S3UploaderService {


      @Value("${cloud.aws.s3.bucket}")
      private String bucket;
  
      @Value("${cloud.aws.region.static}")
      private String region;
  
      private final AmazonS3 amazonS3;
      private final ImageRepogitory imageRepogitory;
  
      @Transactional
      public List<String> insertFile(List<MultipartFile> multipartFiles){
  
          List<String> fileURLs = new ArrayList<>();
  
          multipartFiles.forEach(files -> {
              String fileName = convertFileName(files.getOriginalFilename());
              String URLs = "https://" + bucket + ".s3." + region + ".amzonaws.com/" + fileName;
  
              ObjectMetadata objectMetadata = new ObjectMetadata();
              objectMetadata.setContentLength(files.getSize());
              objectMetadata.setContentType(files.getContentType());
  
              try(InputStream inputStream = files.getInputStream()){
                  amazonS3.putObject(new PutObjectRequest(bucket,fileName,inputStream,objectMetadata));
                  imageRepogitory.save(new ImageDto(URLs).toEntity());
                  fileURLs.add(URLs);
              }catch (IOException e){
                  throw  new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드 실패!!");
              }
          });
          return fileURLs;
      }
  
      /**
       * 파일 이름을 랜덤으로 형성
       * @param fileName
       * @return 랜덤 생성 UUID-파일이름.jpg,
       */
      public String convertFileName(String fileName){
          return UUID.randomUUID().toString().concat(getFileExtension(fileName));
      }
  
      /**
       * 파일의 확장자 이름을 획득
       * @param fileName 파일이름
       * @return 파일 확장자
       */
      private String getFileExtension(String fileName){
          try{
              return fileName.substring(fileName.lastIndexOf("."));
          }catch (StringIndexOutOfBoundsException e){
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"잘못된 형시의 파일" + fileName);
          }
      }


  }

  ```
</details>
