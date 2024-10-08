package gr.hua.dit.farmerCompensation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Check;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "form")
public class DeclarationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 30)
    private String fieldAddress;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotBlank
    @Size(max = 30)
    private String plant_production;


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/mm/yyyy")
    private Date annualStartProduction;


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/mm/yyyy")
    private Date submissionDate;

    @NotBlank
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$",message = "Size of the field in acre.")
    private String fieldSize;


    @NotBlank
    private String naturalDisaster;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/mm/yyyy")
    private Date damageDate;

    @Check(constraints = "status IN ('Pending', 'Accepted', 'Rejected', 'Check on site')")
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'Pending'")
    private String status;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$",message = "Amount in Euros")
    private String amount;


    private String rejectCause;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/mm/yyyy")
    private Date appointementDate;

    @Column(precision = 5, scale = 4)
    private BigDecimal damagePercentage;

    @NotBlank
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$",message = "Amount in Euros")
    private String annualRevenues;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$",message = "Amount in Euros")
    private String estimatedRefund;

    //constructor
    public DeclarationForm(String fieldAddress, String description, String plant_production, Date annualStartProduction, Date submissionDate, String fieldSize, Date damageDate, User user,String amount,String status, String naturalDisaster, String rejectCause, Date appointementDate, BigDecimal damagePercentage,String annualRevenues,String estimatedRefund ) {
        this.fieldAddress = fieldAddress;
        this.description = description;
        this.plant_production = plant_production;
        this.annualStartProduction = annualStartProduction;
        this.submissionDate = submissionDate;
        this.fieldSize = fieldSize;
        this.damageDate = damageDate;
        this.status=status;
        this.amount=amount;
        this.naturalDisaster= naturalDisaster;
        this.rejectCause = rejectCause;
        this.appointementDate = appointementDate;
        this.damagePercentage= damagePercentage;
        this.annualRevenues=annualRevenues;
        this.estimatedRefund=estimatedRefund;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //many to one relationship with user
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    public DeclarationForm() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldAddress() {
        return fieldAddress;
    }

    public void setFieldAddress(String fieldAddress) {
        this.fieldAddress = fieldAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlant_production() {
        return plant_production;
    }

    public void setPlant_production(String plant_production) {
        this.plant_production = plant_production;
    }

    public Date getAnnualStartProduction() {
        return annualStartProduction;
    }

    public void setAnnualStartProduction(Date annualStartProduction) {
        this.annualStartProduction = annualStartProduction;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(String fieldSize) {
        this.fieldSize = fieldSize;
    }

    public Date getDamageDate() {
        return damageDate;
    }

    public void setDamageDate(Date damageDate) {
        this.damageDate = damageDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNaturalDisaster() {
        return naturalDisaster;
    }

    public void setNaturalDisaster(String naturalDisaster) {
        this.naturalDisaster = naturalDisaster;
    }

    public String getRejectCause() {
        return rejectCause;
    }

    public void setRejectCause(String rejectCause) {
        this.rejectCause = rejectCause;
    }

    public Date getAppointementDate() {
        return appointementDate;
    }

    public void setAppointementDate(Date appointementDate) {
        this.appointementDate = appointementDate;
    }

    public BigDecimal getDamagePercentage() {
        return damagePercentage;
    }

    public void setDamagePercentage(BigDecimal damagePercentage) {
        this.damagePercentage = damagePercentage;
    }

    public String getAnnualRevenues() {
        return annualRevenues;
    }

    public void setAnnualRevenues(String annualRevenues) {
        this.annualRevenues = annualRevenues;
    }

    public String getEstimatedRefund() {
        return estimatedRefund;
    }

    public void setEstimatedRefund(String estimatedRefund) {
        this.estimatedRefund = estimatedRefund;
    }

    @Override
    public String toString() {
        return "ApplicationForm{" +
                "id=" + id +
                ", fieldAddress='" + fieldAddress + '\'' +
                ", description='" + description + '\'' +
                ", plant_production='" + plant_production + '\'' +
                ", annualStartProduction=" + annualStartProduction +
                ", submissionDate=" + submissionDate +
                ", fieldSize=" + fieldSize +
                ", damageDate=" + damageDate +
                ", naturalDisaster"+ naturalDisaster+
                '}';
    }
}



