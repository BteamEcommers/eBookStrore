package eBook.EatBook.domain.book.service;

import eBook.EatBook.domain.book.entity.Book;
import eBook.EatBook.domain.book.entity.FileUploadUtil;
import eBook.EatBook.domain.book.repository.BookRepository;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> getList() {
        return this.bookRepository.findAll();
    }

    public Book getList(Integer id) {
        Optional<Book> book = this.bookRepository.findById(id);
        if (book.isEmpty()) {
            return null;
        }
        return book.get();
    }

    public Book createWithImage(String subject, String content,
                                String bookIntroduce, String author, Category category,
                                Integer price, Float discount, String publisher
            , MultipartFile image) throws IOException {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        Book book = Book.builder()
                .subject(subject)
                .content(content)
                .bookIntroduce(bookIntroduce)
                .author(author)
                .category((eBook.EatBook.domain.category.entity.Category) category)
                .price(price)
                .discount(discount)
                .publisher(publisher)
                .bookThumbnailImg(fileName)
                .build();
        bookRepository.save(book);

        String uploadDir = "book-thumbnails/" + book.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, image);

        return book;
    }
}
