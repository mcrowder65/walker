import {InitialState} from './initial-state';
import {Actions} from './actions';
import {State} from '../typings/state';
import {Action} from '../typings/action';

export function rootReducer(state: State = InitialState, action: Action): State {
    switch(action.type) {
        case 'SHOW_MAIN_SPINNER': {
            return {
              ...state,
              mainViewToShow: 'spinner'
            };
        }
        case 'HIDE_MAIN_SPINNER': {
            const newState = Object.assign({}, state);

            newState.mainViewToShow = 'routes';

            return newState;
        }
        case 'SET_COURSE_COLLABORATOR_EMAILS': {
            const newState = Object.assign({}, state);

            if (newState.courseCollaboratorEmails[action.uid]) {
                newState.courseCollaboratorEmails[action.uid][action.courseId] = action.emails;
            }
            else {
                newState.courseCollaboratorEmails[action.uid] = {
                    [action.courseId]: action.emails
                };
            }

            return newState;
        }
        case 'SET_CONCEPT_COLLABORATOR_EMAILS': {
            const newState = Object.assign({}, state);

            if (newState.conceptCollaboratorEmails[action.courseId]) {
                newState.conceptCollaboratorEmails[action.courseId][action.conceptId] = action.emails;
            }
            else {
                newState.conceptCollaboratorEmails[action.courseId] = {
                    [action.conceptId]: action.emails
                };
            }

            return newState;
        }
        case 'SET_VIDEO_COLLABORATOR_EMAILS': {
            const newState = Object.assign({}, state);

            if (newState.videoCollaboratorEmails[action.conceptId]) {
                newState.videoCollaboratorEmails[action.conceptId][action.videoId] = action.emails;
            }
            else {
                newState.videoCollaboratorEmails[action.conceptId] = {
                    [action.videoId]: action.emails
                };
            }

            return newState;
        }
        case 'SET_QUIZ_COLLABORATOR_EMAILS': {
            const newState = Object.assign({}, state);

            if (newState.quizCollaboratorEmails[action.conceptId]) {
                newState.quizCollaboratorEmails[action.conceptId][action.quizId] = action.emails;
            }
            else {
                newState.quizCollaboratorEmails[action.conceptId] = {
                    [action.quizId]: action.emails
                };
            }

            return newState;
        }
        case 'SET_SHARED_COURSES': {
            const newState = Object.assign({}, state);

            newState.sharedCourses = action.courses;

            return newState;
        }
        case 'SET_STARRED_COURSES': {
            const newState = Object.assign({}, state);

            newState.starredCourses = action.courses;

            return newState;
        }
        case 'SET_COURSES_BY_VISIBILITY': {
            const newState = Object.assign({}, state);

            if (action.visibility === 'public') {
                newState.publicCourses = action.courses;
            }

            return newState;
        }
        case 'LOAD_EDIT_CONCEPT_QUIZZES': {
            const newState = Object.assign({}, state);
            newState.editConceptQuizzes[action.conceptId] = action.quizzes;
            return newState;
        }
        case 'LOAD_VIEW_CONCEPT_QUIZZES': {
            const newState = Object.assign({}, state);
            newState.viewConceptQuizzes[action.conceptId] = action.quizzes;
            return newState;
        }
        case 'SET_CURRENT_EDIT_QUIZ_ID': {
            const newState = Object.assign({}, state);
            newState.currentEditQuizId = action.quizId;
            return newState;
        }
        case 'LOAD_QUIZ_SETTINGS': {
            const newState = Object.assign({}, state);
            newState.quizQuestionSettings = action.quizQuestionSettings;
            return newState;
        }
        case 'LOAD_QUIZ_QUESTION_IDS': {
            const newState = Object.assign({}, state);
            newState.quizQuestionIds = action.quizQuestionIds;
            return newState;
        }
        case 'LOAD_USER_QUESTION_IDS': {
            const newState = Object.assign({}, state);
            newState.userQuestionIds = action.userQuestionIds;
            return newState;
        }
        case 'LOAD_PUBLIC_QUESTION_IDS': {
            const newState = Object.assign({}, state);
            newState.publicQuestionIds = action.publicQuestionIds;
            return newState;
        }
      case 'CHECK_USER_AUTH': {
        const newState = Object.assign({}, state);
        newState.currentUser = action.user;
        newState.jwt = action.jwt;
        return newState;
      }
      case 'GET_CONCEPT_BY_ID': {
        const newState = Object.assign({}, state);
        newState.currentConcept = action.concept;
        return newState;
      }
      case 'DELETE_CONCEPT': {
        const newState = {
          ...state,
          currentCourseViewCourse: action.currentCourse
        }
        delete newState.concepts[action.conceptKey];
        return newState;
      }
      case 'UPDATE_USER_META_DATA': {
        const newState = Object.assign({}, state);
        newState.currentUser.metaData = action.userMetaData;
        return newState;
      }
      case 'LOAD_EDIT_CONCEPT_VIDEOS': {
          const newState = Object.assign({}, state);
          newState.editConceptVideos[action.conceptId] = action.videos;
          return newState;
      }
      case 'LOAD_VIEW_CONCEPT_VIDEOS': {
          const newState = Object.assign({}, state);
          newState.viewConceptVideos[action.conceptId] = action.videos;
          return newState;
      }
      case 'LOAD_EDIT_COURSE_CONCEPTS': {
          const newState = Object.assign({}, state);
          newState.editCourseConcepts[action.courseId] = action.concepts;
          return newState;
      }
      case 'LOAD_VIEW_COURSE_CONCEPTS': {
        const newState = Object.assign({}, state);
        newState.viewCourseConcepts[action.courseId] = action.orderedConcepts;
        return newState;

      }
      case 'SET_CURRENT_VIDEO_INFO': {
          const newState = Object.assign({}, state);
          newState.currentConceptVideoId = action.id;
          newState.currentConceptVideoTitle = action.title;
          newState.currentConceptVideoUrl = action.url;
          return newState;
      }
      case 'CLEAR_CURRENT_VIDEO_INFO': {
          const newState = Object.assign({}, state);
          newState.currentConceptVideoId = null;
          newState.currentConceptVideoTitle = '';
          newState.currentConceptVideoUrl = '';
          return newState;
      }
      case 'SET_CURRENT_VIDEO_ID': {
          const newState = Object.assign({}, state);
          newState.currentConceptVideoId = action.id;
          return newState;
      }
      case 'GET_COURSE_BY_ID' : {
        const newState = Object.assign({}, state);
        newState.courseViewCurrentCourse = action.currentCourse;
        return newState;
      }
      case 'GET_COURSES_BY_USER': {
        const newState = Object.assign({}, state);
        newState.courses = action.courses;
        return newState;
      }
      case 'LOOKUP_CONCEPT_TAGS': {
        const newState = Object.assign({}, state);
        newState.resultingConcepts = action.conceptsArray;
        return newState;
      }
      case 'SET_COURSE_TAGS': {
        const newState = Object.assign({}, state);
        newState.resultingCourses = action.coursesArray;
        return newState;
      }
      case 'SET_COURSE_VIEW_CURRENT_COURSE': {
        const newState = Object.assign({}, state);
        newState.courseViewCurrentCourse = action.currentCourse;
        newState.courseTagNames = action.courseTagNames;
        return newState;
      }
      case 'ADD_TAG_EDIT_COURSE': {
        const newState = Object.assign({}, state);
        newState.courseViewCurrentCourse = action.currentCourse;
        newState.courseTagNames = action.courseTagNames;
        return newState;
      }
      case 'DELETE_TAG_EDIT_COURSE': {
        const newState = Object.assign({}, state);
        newState.courseViewCurrentCourse = action.currentCourse;
        newState.courseTagNames = action.courseTagNames;
        return newState;
      }
      case 'ADD_COURSE': {
        const newState = Object.assign({}, state);
        newState.courses = action.courses;
        newState.sharedCourses = action.courses;
        return newState;
      }
      case 'DELETE_COURSE': {
        const newState = Object.assign({}, state);
        newState.courses = action.courses;
        newState.sharedCourses = action.courses;
        return newState;
      }
      case 'ADD_CONCEPT': {
        const newState = Object.assign({}, state);
        newState.currentCourse = action.currentCourse;
        return newState;
      }
      case 'RELOAD_PUBLIC_COURSES': {
        return {
          ...state,
          publicCourses: action.courses
        };
      }
      default: {
          return state;
      }
    }
}
