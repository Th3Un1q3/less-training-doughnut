Feature: Repetition Picture Quiz
    As a learner, I want to use quizzes in my repetition to help and gamify my learning.

    Background:
        Given I've logged in as an existing user
        And there are some notes for the current user
            | title      | skipReview  | testingParent  |
            | Space      | true        |                |
        When I create note belonging to "Space":
            | note-title   | note-uploadPicture | note-pictureMask      |
            | Earth        | example.png        | 20 40 70 30 40 80 5 8 |
        And I create note belonging to "Space":
            | note-title   | note-uploadPicture | note-pictureMask      |
            | Moon         | moon.jpg           | 30 40 20 30           |

    Scenario: Picture question
        Given I learned one note "Earth" on day 1
        When I am repeat-reviewing my old note on day 2
        Then I should be asked picture question "example.png" with options "Earth, Moon"
        And I should see the screenshot matches

