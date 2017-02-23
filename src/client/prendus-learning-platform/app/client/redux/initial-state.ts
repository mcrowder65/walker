import {QuestionSettings} from '../node_modules/prendus-services/typings/question-settings';
import {State} from '../typings/state';

export const InitialState: State = {
    mainViewToShow: 'routes',
    concepts: {
    },
    currentConcept: {
    },
    courses: [],
    courseViewCurrentCourse: {},
    editCourseConcepts: {},
    viewCourseConcepts: {},
    editConceptVideos: {},
    viewConceptVideos: {},
    currentConceptVideoId: '',
    currentConceptVideoTitle: '',
    currentConceptVideoUrl: '',
    currentUser: {
        authorizedQuestions: {},
        authorizedQuizzes: {},
        metaData: {
            email: '',
            firstName: '',
            lastName: '',
            institution: '',
            uid: ''
        },
        starredCourses: {},
        sharedWithMeCourses: {},
        sharedWithMeConcepts: {},
        sharedWithMeVideos: {},
        sharedWithMeQuizzes: {}
    },
    userQuestionIds: [],
    publicQuestionIds: [],
    quizQuestionIds: [],
    quizQuestionSettings: {},
    currentEditQuizId: '',
    editConceptQuizzes: {},
    viewConceptQuizzes: {},
    currentEditConceptId: '',
    publicCourses: [],
    starredCourses: [],
    sharedCourses: [],
    courseCollaboratorEmails: {},
    conceptCollaboratorEmails: {},
    videoCollaboratorEmails: {},
    quizCollaboratorEmails: {}
};
