package qna.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import qna.CannotDeleteException;
import qna.ErrorMessage;
import qna.NotFoundException;
import qna.UnAuthorizedException;

@Entity
public class Answer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_answer_writer"))
	private User writer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", foreignKey = @ForeignKey(name = "fk_answer_to_question"))
	private Question question;

	@Lob
	private String contents;

	@Column(nullable = false)
	private boolean deleted = false;

	protected Answer() {
	}

	public Answer(User writer, Question question, String contents) {
		this(null, writer, question, contents);
	}

	public Answer(Long id, User writer, Question question, String contents) {
		this.id = id;

		if (Objects.isNull(writer)) {
			throw new UnAuthorizedException();
		}

		if (Objects.isNull(question)) {
			throw new NotFoundException();
		}

		this.writer = writer;
		this.question = question;
		this.contents = contents;
	}

	private boolean isOwner(User writer) {
		return this.writer.equals(writer);
	}

	public DeleteHistory delete(User loginUser) throws CannotDeleteException {
		if (!isOwner(loginUser)) {
			throw new CannotDeleteException(ErrorMessage.CAN_NOT_DELETE_ANSWER_WITHOUT_OWNERSHIP.getContent());
		}
		return delete();
	}

	public Long getId() {
		return id;
	}

	public User getWriter() {
		return writer;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getContents() {
		return contents;
	}

	public boolean isDeleted() {
		return deleted;
	}

	private DeleteHistory delete() {
		this.deleted = true;
		return new DeleteHistory(ContentType.ANSWER, getId(), getWriter());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Answer))
			return false;
		Answer answer = (Answer)o;
		return deleted == answer.deleted && Objects.equals(id, answer.id) && Objects.equals(writer,
			answer.writer) && Objects.equals(question, answer.question) && Objects.equals(contents,
			answer.contents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, writer, question, contents, deleted);
	}

	@Override
	public String toString() {
		return "Answer{" +
			"id=" + id +
			", writer=" + writer +
			", question=" + question +
			", contents='" + contents + '\'' +
			", deleted=" + deleted +
			'}';
	}
}
