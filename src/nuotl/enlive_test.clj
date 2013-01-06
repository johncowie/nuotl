(ns nuotl.enlive-test
    (:use [net.cgrand.enlive-html :only [html-resource select]]))

(def page (html-resource "public/templates/test.html"))

(select page [:tr.feminism :td])	
