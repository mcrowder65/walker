import {Video} from '../node_modules/prendus-services/typings/video';
import {Quiz} from '../node_modules/prendus-services/typings/quiz';
import {User} from '../node_modules/prendus-services/typings/user';
import {UserMetaData} from '../node_modules/prendus-services/typings/user-meta-data';
import {Course} from '../node_modules/prendus-services/typings/course';
import {CourseConceptData} from '../node_modules/prendus-services/typings/course-concept-data';

export interface State {
    editCourseConcepts: {
        [courseId: string]: CourseConceptData[]
    };
    editConceptVideos: {
        [conceptId: string]: Video[]
    };
    editConceptQuizzes: {
        [conceptId: string]: Quiz[]
    };
    viewCourseConcepts: {
        [courseId: string]: CourseConceptData[]
    };
    viewConceptVideos: {
        [conceptId: string]: Video[]
    };
    viewConceptQuizzes: {
        [conceptId: string]: Quiz[]
    };
    courses: Course[];
    userCourses: Course[];
    sharedCourses: Course[];
    starredCourses: Course[];
    publicCourses: Course[];
    currentConcept: {};
    concepts: {};
    courseTagNames: string[];
    courseViewCurrentCourse: Course;
    currentConceptVideoId: string;
    currentConceptVideoTitle: string;
    currentConceptVideoUrl: string
    currentUser: {
      metaData: UserMetaData
    };
    currentEditQuizId: '';
    currentEditConceptId: '';
    jwt: string;
    courseCollaboratorEmails: {
        [uid: string]: {
            [courseId: string]: string[];
        };
    };
    conceptCollaboratorEmails: {
        [courseId: string]: {
            [conceptId: string]: string[];
        };
    };
    videoCollaboratorEmails: {
        [conceptId: string]: {
            [videoId: string]: string[];
        };
    };
    quizCollaboratorEmails: {
        [conceptId: string]: {
            [quizId: string]: string[];
        };
    };
    mainViewToShow: 'routes' | 'spinner';
}
