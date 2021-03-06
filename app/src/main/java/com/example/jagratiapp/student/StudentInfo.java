package com.example.jagratiapp.student;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.jagratiapp.R;
import com.example.jagratiapp.model.Students;
import com.example.jagratiapp.student.Util.StudentAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;

public class StudentInfo extends AppCompatActivity {
    private String studID;
    private String classID;
    private String groupID;
    private DocumentReference studentReference;
    private CollectionReference attendanceReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView villageName;
    private TextView className;
    private TextView studentName;
    private TextView guardianName;
    private TextView rollNo;
    private TextView attendance;
    private int present = 0;
    private int totalDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.appbar));

        studentName = findViewById(R.id.student_name_info);
        rollNo = findViewById(R.id.roll_no_info);
        villageName = findViewById(R.id.village_name_info);
        className = findViewById(R.id.class_name_info);
        guardianName = findViewById(R.id.guardian_name_info);
        attendance = findViewById(R.id.attendance_info);

        studentReference = db.collection("Classes").document(StudentAPI.Instance().getClassUid())
                .collection("Groups").document(StudentAPI.Instance().getGroupUid()).collection("Students").document(StudentAPI.Instance().getRollno());

        attendanceReference = db.collection("Classes").document(classID)
                .collection("Groups").document(groupID).collection("Attendance");

        studentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Students student =  documentSnapshot.toObject(Students.class);
                villageName.setText(String.format("Village Name: %s", student.getVillageName()));
                className.setText(String.format("Class Name: %s", student.getClassName()));
                studentName.setText(student.getStudentName());
                rollNo.setText(String.format("Roll No: %s", student.getRollno()));
                guardianName.setText(String.format("Guardian Name: %s", student.getGuardianName()));
            }
        });

        attendanceReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot groupDocumentSnapshot : queryDocumentSnapshots){
                    totalDays++;
                    if(groupDocumentSnapshot.getBoolean(studID)){
                        present++;
                    }
                }
                attendance.setText(MessageFormat.format("Attendance: {0} Out of {1} days", present, totalDays));
            }
        });

    }
}